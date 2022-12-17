package com.gallerydemo.ui.main.folder

import android.content.ContentResolver
import android.database.Cursor
import android.net.Uri
import android.provider.MediaStore
import com.gallerydemo.R
import com.gallerydemo.data.local.models.GalleryFolder
import com.gallerydemo.data.local.models.MediaItem
import com.gallerydemo.utils.*
import com.gallerydemo.utils.callback.StringResProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject


class GalleryRepository @Inject constructor() {

    fun loadMediaFromStorage(
        contentResolver: ContentResolver,
        @GalleryModes galleryMode: Int = GALLERY_IMAGE_AND_VIDEOS,
        stringProvider: StringResProvider
    ): Flow<State<List<GalleryFolder>>> {
        return flow {
            emit(State.Loading)
            val data = executeContentResolver(contentResolver, galleryMode, stringProvider)
            if (data != null)
                emit(State.Success(data))
            else
                emit(State.Failure())
        }.flowOn(Dispatchers.IO)
    }

    private fun getSelectionQuery(@GalleryModes galleryMode: Int): String {
        return when (galleryMode) {
            GALLERY_IMAGE_AND_VIDEOS -> StringBuilder().append(MediaStore.Files.FileColumns.MEDIA_TYPE)
                .append(" IN(")
                .append(MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE)
                .append(",")
                .append(MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO)
                .append(")").toString()
            GALLERY_VIDEO -> StringBuilder().append(MediaStore.Files.FileColumns.MEDIA_TYPE)
                .append("=")
                .append(MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO).toString()
            else /*GALLERY_IMAGE*/ -> StringBuilder().append(MediaStore.Files.FileColumns.MEDIA_TYPE)
                .append("=")
                .append(MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE).toString()
        }
    }

    private fun getProjectionArray() = arrayOf(
        MediaStore.MediaColumns._ID,
        MediaStore.MediaColumns.TITLE,
        MediaStore.MediaColumns.MIME_TYPE,
        MediaStore.MediaColumns.WIDTH,
        MediaStore.MediaColumns.HEIGHT,
        MediaStore.MediaColumns.SIZE,
        MediaStore.Images.Media.BUCKET_DISPLAY_NAME
    )

    /**
     * This function is to execute query of contentResolver, handle and parse response and return
     * list of gallery media folders or null in case of some exception
     */
    private fun executeContentResolver(
        contentResolver: ContentResolver,
        @GalleryModes galleryMode: Int,
        stringProvider: StringResProvider
    ): List<GalleryFolder>? {
        return kotlin.runCatching {
            val folders = mutableListOf<GalleryFolder>()
            val cursor: Cursor?
            val uri: Uri = MediaStore.Files.getContentUri("external")
            val selection: String = getSelectionQuery(galleryMode)
            val projection = getProjectionArray()
            val orderBy = MediaStore.Images.Media.DATE_TAKEN
            cursor = contentResolver.query(
                uri,
                projection,
                selection,
                null,
                "$orderBy DESC"
            )
            if (cursor != null) {
                val foldersMap =
                    mutableMapOf<String, GalleryFolder>() // key is folderName here we can group media by folder names
                val allImageMedia: MutableList<MediaItem> by lazy { mutableListOf() }
                val allVideoMedia: MutableList<MediaItem> by lazy { mutableListOf() }
                val canAddImages =
                    galleryMode != GALLERY_VIDEO // if not restricted to have videos only
                val canAddVideos =
                    galleryMode != GALLERY_IMAGE // if not restricted to have images only
                while (cursor.moveToNext()) {
                    cursorToGalleryMedia(cursor)?.let { (folderName, mediaItem) ->

                        if (canAddVideos && mediaItem.isVideo)
                            allVideoMedia.add(mediaItem)
                        else if (canAddImages && !mediaItem.isVideo)
                            allImageMedia.add(mediaItem)

                        /* if has folder model already added then append media to that, else create
                        a new folder and add media into that */
                        foldersMap.getOrElse(folderName) {
                            foldersMap.put(
                                folderName,
                                GalleryFolder(folderName, mutableListOf())
                            )
                        }?.mediaList?.add(mediaItem)

                    }
                }
                if (canAddImages)
                    folders.add(
                        GalleryFolder(
                            stringProvider.getString(R.string.all_images),
                            allImageMedia
                        )
                    )
                if (canAddVideos)
                    folders.add(
                        GalleryFolder(
                            stringProvider.getString(R.string.all_videos),
                            allVideoMedia
                        )
                    )
                folders.addAll(foldersMap.values)
            }
            folders
        }.getOrNull()
    }

    /**
     * This function will parse cursor to MediaItem and return its folder name along with it
     */
    private fun cursorToGalleryMedia(cursor: Cursor?): Pair<String, MediaItem>? {
        return cursor?.let {
            kotlin.runCatching {
                Pair(
                    it.getString(it.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME)),
                    MediaItem(
                        id = it.getInt(it.getColumnIndexOrThrow(MediaStore.Images.Media._ID)),
                        width = it.getInt(it.getColumnIndexOrThrow(MediaStore.Images.Media.WIDTH)),
                        height = it.getInt(it.getColumnIndexOrThrow(MediaStore.Images.Media.HEIGHT)),
                        path = getUriFromPathSegment(
                            cursor.getLong(
                                cursor.getColumnIndexOrThrow(
                                    MediaStore.Images.Media._ID
                                )
                            )
                        ).toString(),
                        size = it.getLong(it.getColumnIndexOrThrow(MediaStore.Images.Media.SIZE)),
                        mimeType = it.getString(it.getColumnIndexOrThrow(MediaStore.Images.Media.MIME_TYPE))
                    )
                )
            }.getOrNull()
        }
    }

    private fun getUriFromPathSegment(mediaId: Long): Uri {
        return Uri.withAppendedPath(MediaStore.Files.getContentUri("external"), mediaId.toString())
    }
}