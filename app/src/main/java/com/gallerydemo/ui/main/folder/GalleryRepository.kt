package com.gallerydemo.ui.main.folder

import android.content.ContentResolver
import android.database.Cursor
import android.net.Uri
import android.provider.MediaStore
import com.gallerydemo.data.local.models.GalleryFolder
import com.gallerydemo.data.local.models.MediaItem
import com.gallerydemo.utils.GALLERY_IMAGE_AND_VIDEOS
import com.gallerydemo.utils.GALLERY_VIDEO
import com.gallerydemo.utils.GalleryModes
import com.gallerydemo.utils.State
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject


class GalleryRepository @Inject constructor() {

    fun loadMediaFromStorage(
        contentResolver: ContentResolver,
        @GalleryModes galleryMode: Int = GALLERY_IMAGE_AND_VIDEOS
    ): Flow<State<List<GalleryFolder>>> {
        return flow {
            emit(State.Loading)
            val data = executeContentResolver(contentResolver, galleryMode)
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

    private fun executeContentResolver(
        contentResolver: ContentResolver,
        @GalleryModes galleryMode: Int
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
                val foldersMap = mutableMapOf<String, GalleryFolder>()
                val allImageMedia = mutableListOf<MediaItem>()
                val allVideoMedia = mutableListOf<MediaItem>()
                while (cursor.moveToNext()) {
                    cursorToGalleryMedia(cursor)?.let { (folderName, mediaItem) ->
                        foldersMap.getOrElse(folderName) {
                            foldersMap.put(
                                folderName,
                                GalleryFolder(folderName, mutableListOf())
                            )
                        }?.mediaList?.add(mediaItem)

                        if (mediaItem.isVideo)
                            allVideoMedia.add(mediaItem)
                        else
                            allImageMedia.add(mediaItem)
                    }
                }
                folders.add(GalleryFolder("All Images", allImageMedia))
                folders.add(GalleryFolder("All Videos", allVideoMedia))
                folders.addAll(foldersMap.values)
            }
            folders
        }.getOrNull()
    }

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