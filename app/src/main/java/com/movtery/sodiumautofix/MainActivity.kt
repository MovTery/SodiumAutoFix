package com.movtery.sodiumautofix

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.lifecycleScope
import com.movtery.anim.AnimPlayer
import com.movtery.anim.animations.Animations
import com.movtery.sodiumautofix.databinding.ActivityMainBinding
import com.movtery.sodiumautofix.fix.ModifyJarFile
import com.movtery.sodiumautofix.fix.OnFixListener
import com.movtery.sodiumautofix.modloader.Mod
import com.movtery.sodiumautofix.sodium.SodiumVersionCheck
import com.movtery.sodiumautofix.utils.CheckFile
import com.movtery.sodiumautofix.utils.FileTools
import com.movtery.sodiumautofix.utils.OnCheckListener
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.apache.commons.io.FileUtils
import java.io.File
import java.util.StringJoiner

class MainActivity : ComponentActivity() {
    private lateinit var binding: ActivityMainBinding
    private val animPlayer = AnimPlayer()
    private val progressAnimPlayer = AnimPlayer()
    private var mCurrentMod: Mod? = null

    private val openDocumentLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                result.data?.data?.let { uri ->
                    binding.startFix.isClickable = false
                    lifecycleScope.launch {
                        FileUtils.deleteQuietly(externalCacheDir!!)
                        showFileInfo()
                        runCatching {
                            val file = FileTools.copyFile(this@MainActivity, uri, externalCacheDir!!.absolutePath)
                            binding.selectedFileName.text = file.name
                            checkFile(file)
                        }.getOrElse {
                            binding.selectedFileName.text = FileTools.getFileName(this@MainActivity, uri)
                            setFileInvalid()
                        }
                    }
                }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.apply {
            selectFile.setOnClickListener {
                if (!isProgressRunning()) openFileSelector()
            }
            startFix.setOnClickListener {
                if (!isProgressRunning()) startFixProcess()
            }
        }

        // 清理缓存
        lifecycleScope.launch(Dispatchers.IO) {
            FileUtils.deleteQuietly(externalCacheDir!!)
        }
    }

    private fun openFileSelector() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "*/*"
        //暂时没有好的办法用于筛选jar文档，所以允许选择选择一切文件，后面通过检查后缀，以及文件特征来判断是否为Mod文件
        }
        openDocumentLauncher.launch(intent)
    }

    private fun startFixProcess() {
        mCurrentMod?.let { mod ->
            lifecycleScope.launch(Dispatchers.IO) {
                val file = File(externalCacheDir!!, mod.getFileName()!!)
                ModifyJarFile.modifySodiumMixinsInJar(this@MainActivity, file, mod,
                    object : OnFixListener {
                        override fun onFixStarted() {
                            setProgressBar(true)
                        }

                        override fun onFixEnded() {
                            setProgressBar(false)
                            showToast(R.string.fix_end)
                            FileTools.shareFile(this@MainActivity, file)
                        }

                        override fun onError(e: Throwable) {
                            setProgressBar(false)
                            showToast(R.string.fix_error, e.toString())
                        }
                    }) { progressText ->
                    runOnUiThread {
                        binding.startProgressText.text = progressText
                    }
                }
            }
        }
    }


    private fun checkFile(file: File) {
        CheckFile.checkFile(file, object : OnCheckListener {
            override fun onEnd(mod: Mod) {
                mCurrentMod = mod.apply { setFileName(file.name) }

                val versionStatus = SodiumVersionCheck.checkVersion(mod.modVersion)
                if (versionStatus in 1 until SodiumVersionCheck.UNSUPPORTED) {
                    updateModInfoUI(mod)
                    setFileValid()
                } else {
                    setFileInvalid(
                        if (versionStatus == SodiumVersionCheck.UNSUPPORTED) R.string.unsupported
                        else R.string.none
                    )
                }
            }

            override fun onFail() {
                setFileInvalid()
            }
        })
    }

    private fun updateModInfoUI(mod: Mod) = binding.apply {
        selectedFileModloader.text = mod.modLoader
        selectedFileModId.text = mod.modId
        selectedFileModVersion.text = mod.modVersion
        selectedFileMixinFile.text = StringJoiner(", ").apply {
            mod.mixinFile.forEach { add(it) }
        }.toString()
    }

    private fun showFileInfo() {
        binding.selectedFileLayout.apply {
            if (alpha == 0f) {
                AnimPlayer.play().apply(AnimPlayer.Entry(this, Animations.BounceEnlarge)).start()
            }
        }
    }

    private fun setFileInvalid(resString: Int = R.string.no) = binding.apply {
        selectedFileValid.text = getString(resString)
        setModInfoVisibility(View.GONE)
        startFix.isClickable = false
        animateButton(startFix, Animations.BounceShrink, false)
    }

    private fun setFileValid() = binding.apply {
        selectedFileValid.text = getString(R.string.yes)
        setModInfoVisibility(View.VISIBLE)
        startFix.isClickable = true
        animateButton(startFix, Animations.BounceEnlarge, true)
    }

    private fun setModInfoVisibility(visibility: Int) = binding.apply {
        selectedFileModloaderLayout.visibility = visibility
        selectedFileModIdLayout.visibility = visibility
        selectedFileModVersionLayout.visibility = visibility
        selectedFileMixinFileLayout.visibility = visibility
    }

    private fun animateButton(view: View, animation: Animations, shouldShow: Boolean) {
        if ((shouldShow && view.alpha == 0f) || (!shouldShow && view.alpha == 1f)) {
            runOnUiThread {
                animPlayer.clearEntries()
                animPlayer.apply(AnimPlayer.Entry(view, animation)).start()
            }
        }
    }

    private fun setProgressBar(show: Boolean) = binding.startProgress.apply {
        val animation = if (show) Animations.FadeIn else Animations.FadeOut
        runOnUiThread {
            progressAnimPlayer.clearEntries()
            progressAnimPlayer.apply(AnimPlayer.Entry(this, animation)).start()
        }
    }

    private fun isProgressRunning() = binding.startProgress.alpha != 0f

    private fun showToast(messageRes: Int, vararg formatArgs: Any) {
        runOnUiThread {
            Toast.makeText(this, getString(messageRes, *formatArgs), Toast.LENGTH_SHORT).show()
        }
    }
}
