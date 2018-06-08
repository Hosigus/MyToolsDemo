package com.hosigus.demo

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.support.v4.content.FileProvider
import com.hosigus.tools.interfaces.DownloadListener
import com.hosigus.tools.interfaces.PermissionCallback
import com.hosigus.tools.options.DownloadOption
import com.hosigus.tools.options.PermissionOption
import com.hosigus.tools.utils.*
import kotlinx.android.synthetic.main.activity_download_manager.*
import java.io.File
import java.util.*

class DownloadManagerActivity : AppCompatActivity() {

    val DOWNLOAD_CODE = 123

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_download_manager)

        val fileName = "zscy.apk"

        val option = DownloadOption(
                "http://a7.pc6.com/hyt6/zhangshangzhongy.apk",//我找不到掌邮地址了
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).absolutePath,fileName)
                .listener(object : DownloadListener {
                    override fun onDownloading(progress: Int, size: Int) {
                        val progressStr = String.format(Locale.CHINA,
                                "共 %.2f M,已下载 %.2f M", size / 1024.0 / 1024, progress / 1024.0 / 1024)
                        tv_progress.text = progressStr
                    }

                    override fun onSuccess(download:File) {
                        show("download success!")
                        // you may want to try x.jpg/x.png
//                        iv_image.background = Drawable.createFromPath(download.absolutePath)
                        val file = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), fileName)
                        val fileUri: Uri
                        fileUri = if (Build.VERSION.SDK_INT >= 24) {
                            FileProvider.getUriForFile(this@DownloadManagerActivity, "com.hosigus.demo.fileProvider", file)
                        } else {
                            Uri.fromFile(file)
                        }
                        val intent = Intent("android.intent.action.VIEW")
                        intent.addCategory("android.intent.category.DEFAULT")
                        intent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
                        intent.setDataAndType(fileUri, "application/vnd.android.package-archive")
                        this@DownloadManagerActivity.startActivity(intent)
                    }
                })

        val manager = DownloadManager(this, option)

        addOption(PermissionOption(DOWNLOAD_CODE)
                .permissions(arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE))
                .callback(object : PermissionCallback {
                    override fun onGranted() {
                        manager.download()
                    }

                    override fun onDenied(DeniedPermissions: Array<String>) {
                        show("come on,I need it")
                    }
                }))


        btn_download.setOnClickListener{
            executeOption(this@DownloadManagerActivity, DOWNLOAD_CODE)
        }

        btn_pause.setOnClickListener {
            manager.pause()
        }

        btn_cancel.setOnClickListener{
            manager.cancel()
            manager.delete()
            tv_progress.text = "下载已取消"
        }

    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        onPermissionRequest(requestCode,permissions,grantResults)
    }

}
