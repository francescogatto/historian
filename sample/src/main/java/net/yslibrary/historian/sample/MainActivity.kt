package net.yslibrary.historian.sample

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Environment
import android.support.design.widget.FloatingActionButton
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast

import net.yslibrary.historian.Historian

import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream
import java.util.concurrent.atomic.AtomicLong

import timber.log.Timber

class MainActivity : AppCompatActivity() {

    internal var counter = AtomicLong()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val toolbar = findViewById(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)

        val fab = findViewById(R.id.fab) as FloatingActionButton
        fab.setOnClickListener {
            var i = 0
            val l = 100
            while (i < 100) {
                //  Timber.i("test: %d", counter.getAndIncrement());
                Timber.tag("TEST").i("test: %d", counter.getAndIncrement())
                i++
                //      Timber.e( new RuntimeException("sjndjf"),"Error");
            }
        }

        val exportButton = findViewById(R.id.export)
        exportButton.setOnClickListener {
            if (ContextCompat.checkSelfPermission(this@MainActivity, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
               // export(this@MainActivity, App.getHistorian(this@MainActivity))
            } else {
                ActivityCompat.requestPermissions(this@MainActivity, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), 0)
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        val id = item.itemId


        return if (id == R.id.action_settings) {
            true
        } else super.onOptionsItemSelected(item)

    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 0) {
            if (Manifest.permission.WRITE_EXTERNAL_STORAGE == permissions[0] && PackageManager.PERMISSION_GRANTED == grantResults[0]) {
              //  export(this, App.getHistorian(this))
            }
        }
    }

  /*  fun export(context: Context, historian: Historian) {
        val dir = File(Environment.getExternalStorageDirectory(), "HistorianSample")
        if (!dir.exists()) {
            dir.mkdir()
        }
        val dbPath = historian.dbPath()
        val exportPath = dir.path + File.separator + historian.dbName()

        var fis: FileInputStream? = null
        var output: OutputStream? = null

        val dbFile = File(dbPath)
        val file = File(exportPath)

        // delete if exists
        file.delete()
        try {
            fis = FileInputStream(dbFile)
            output = FileOutputStream(exportPath)
            val buffer = ByteArray(1024)
            var length: Int
            while ((length = fis.read(buffer)) > 0) output.write(buffer, 0, length)
            //Close the streams
            output.flush()

            Toast.makeText(context, "File exported to: $exportPath", Toast.LENGTH_SHORT).show()
        } catch (e: IOException) {
            e.printStackTrace()
            Toast.makeText(context, "Failed to export", Toast.LENGTH_SHORT).show()
        } finally {
            Util.closeQuietly(output)
            Util.closeQuietly(fis)
        }
    }*/
}
