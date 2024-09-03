package com.spy.logger

import android.Manifest
import android.app.Service
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.ImageFormat
import android.hardware.camera2.CameraAccessException
import android.hardware.camera2.CameraCaptureSession
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraDevice
import android.hardware.camera2.CameraManager
import android.hardware.camera2.CaptureRequest
import android.hardware.camera2.TotalCaptureResult
import android.media.MediaRecorder
import android.os.IBinder
import android.util.Log
import androidx.core.app.ActivityCompat
import java.io.File
import java.io.IOException
import android.os.Handler
import android.media.ImageReader
import android.media.Image
import android.os.HandlerThread
import android.view.Surface
import androidx.camera.lifecycle.ProcessCameraProvider
import com.google.common.util.concurrent.ListenableFuture
import java.io.FileOutputStream
import java.nio.ByteBuffer

class CameraService : Service() {




    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startCapture()
        return START_STICKY
    }

    private fun startCapture() {

        cameraManager = getSystemService(CAMERA_SERVICE) as CameraManager
        try {
            cameraId = cameraManager.cameraIdList[0]
            thread = HandlerThread("CameraBackground")
            thread.start()
            handler = Handler(thread.looper)

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
                return
            }

            cameraManager.openCamera(cameraId, object : CameraDevice.StateCallback() {
                override fun onOpened(camera: CameraDevice) {
                    cameraDevice = camera
                    setupMediaRecorder()
                    startCaptureSession(cameraDevice)
                }

                override fun onDisconnected(camera: CameraDevice) {
                    camera.close()
                }

                override fun onError(camera: CameraDevice, error: Int) {
                    camera.close()
                }
            }, handler)

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    private fun startCaptureSession(cameraDevice: CameraDevice) {

        try {
            // Create a list to hold the Surface
            val surfaces = mutableListOf<Surface>()

            // Get the Surface from MediaRecorder after preparation
            val recorderSurface: Surface = mediaRecorder.surface
            surfaces.add(recorderSurface)

            // Create a capture request builder for video
            val captureRequestBuilder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_RECORD)
            captureRequestBuilder.addTarget(recorderSurface)

            cameraDevice.createCaptureSession(surfaces, object : CameraCaptureSession.StateCallback() {
                override fun onConfigured(session: CameraCaptureSession) {
                    // Start recording when the session is configured
                    mediaRecorder.start()
                    Log.d("CameraCapture", "Recording started")
                }

                override fun onConfigureFailed(session: CameraCaptureSession) {
                    Log.e("CameraCapture", "Configuration failed")
                }
            }, handler)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    private fun setupMediaRecorder() {
        val mediaRecorder = MediaRecorder()
        videoFile = File(getExternalFilesDir(null), "video_${System.currentTimeMillis()}.mp4")

        try {
            mediaRecorder.apply {
                setAudioSource(MediaRecorder.AudioSource.MIC)
                setVideoSource(MediaRecorder.VideoSource.SURFACE)
                setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
                setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
                setVideoEncoder(MediaRecorder.VideoEncoder.H264)
                setOutputFile(videoFile.absolutePath)
                setVideoEncodingBitRate(10000000) // Adjust the bitrate as needed
                setVideoFrameRate(30) // Adjust frame rate as preferred
                setVideoSize(1920, 1080) // Change this to your desired resolution

                prepare()
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }


    override fun onDestroy() {
        super.onDestroy()
//        stopCapture()
    }

//    private fun stopCapture() {
//        mediaRecorder?.apply {
//            try {
//                stop()
//                Log.d("VideoCaptureService", "Capturing Stopped: ${videoFile.absolutePath}")
//            } catch (e: IllegalStateException) {
//                Log.e("CameraService", "Error stopping the media recorder: ${e.message}", e)
//            } finally {
//                release()
//            }
//        }
//        mediaRecorder = null
//        cameraDevice.close()
//    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }
}