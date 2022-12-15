package com.gallerydemo.utils

import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.provider.MediaStore
import com.gallerydemo.data.local.models.MediaItem
import com.gallerydemo.data.local.models.GalleryFolder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class GalleryHelper(private val context: Context) {

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
            GALLERY_IMAGE ->
                StringBuilder().append(MediaStore.Files.FileColumns.MEDIA_TYPE).append("=")
                    .append(MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE).toString()
            else -> StringBuilder().append(MediaStore.Files.FileColumns.MEDIA_TYPE)
                .append("=")
                .append(MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE).toString()
        }
    }

    suspend fun loadGalleryMedia(
        @GalleryModes galleryMode: Int,
        grandFolderName: String,
    ): Set<GalleryFolder> =
        withContext(Dispatchers.IO) {
            val folders = mutableSetOf<GalleryFolder>()

            kotlin.runCatching {
                val cursor: Cursor?
                // Return only video or image metadata.
                val uri: Uri = MediaStore.Files.getContentUri("external")
                val selection: String = getSelectionQuery(galleryMode)
                val projection = arrayOf(
                    MediaStore.MediaColumns._ID,
                    MediaStore.MediaColumns.TITLE,
                    MediaStore.MediaColumns.MIME_TYPE,
                    MediaStore.MediaColumns.WIDTH,
                    MediaStore.MediaColumns.HEIGHT,
                    MediaStore.MediaColumns.SIZE,
                    MediaStore.Images.Media.BUCKET_DISPLAY_NAME
                )
                val orderBy = MediaStore.Images.Media.DATE_TAKEN
                cursor = context.contentResolver?.query(
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
            }
            printLog("usm_gallery_folders", "size= " + folders.size)
            folders
        }

    private fun cursorToGalleryMedia(cursor: Cursor?): Pair<String, MediaItem>? {
        return cursor?.let {
            kotlin.runCatching {
                Pair(it.getString(it.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME)),
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
                        size = it.getLong(it.getColumnIndexOrThrow(MediaStore.Images.Media.SIZE))
                    )
                        .apply {
                            mimeType =
                                it.getString(it.getColumnIndexOrThrow(MediaStore.Images.Media.MIME_TYPE))
                        })
            }.getOrNull()
        }
    }

    private fun getUriFromPathSegment(mediaId: Long): Uri {
        return Uri.withAppendedPath(MediaStore.Files.getContentUri("external"), mediaId.toString())
    }
}