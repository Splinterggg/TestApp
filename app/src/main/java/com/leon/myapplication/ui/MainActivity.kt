package com.leon.myapplication.ui

import android.Manifest
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.leon.myapplication.R
import com.leon.myapplication.databinding.MainActivityBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val viewModel by viewModels<MainViewModel>()

    private var _binding: MainActivityBinding? = null
    private val binding get() = _binding!!
    private var hasNotificationPermissionGranted = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = MainActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)
        lifecycleScope.launchWhenCreated {
            viewModel.bootEntities.collectLatest {
                binding.bootStatusText.text = it
            }
        }
    }

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission(),
    ) { isGranted: Boolean ->
        hasNotificationPermissionGranted = isGranted
        if (isGranted) {
            Toast.makeText(
                applicationContext,
                getString(R.string.notification_permission_granted),
                Toast.LENGTH_SHORT,
            ).show()
            viewModel.runNotificationWorker(hasNotificationPermissionGranted)
        } else {
            showPermissionNeededDialog()
        }
    }

    override fun onResume() {
        super.onResume()
        when {
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS,
            ) == PackageManager.PERMISSION_GRANTED -> {
                hasNotificationPermissionGranted = true
            }

            shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS) -> {
                showLinkToSettings()
            }

            else -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                } else {
                    hasNotificationPermissionGranted = true
                }
            }
        }
        viewModel.runNotificationWorker(hasNotificationPermissionGranted)
    }

    private fun showLinkToSettings() {
        Snackbar.make(
            binding.root,
            getString(R.string.notification_blocked),
            Snackbar.LENGTH_LONG,
        ).setAction(getString(R.string.settings)) {
            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            val uri: Uri = Uri.fromParts("package", packageName, null)
            intent.data = uri
            startActivity(intent)
        }.show()
    }

    private fun showPermissionNeededDialog() {
        MaterialAlertDialogBuilder(
            this,
            com.google.android.material.R.style.MaterialAlertDialog_Material3,
        )
            .setTitle(getString(com.leon.myapplication.R.string.notification_permission))
            .setMessage(getString(R.string.notification_permission_is_required))
            .setPositiveButton(getString(R.string.ok), null)
            .show()
    }
}
