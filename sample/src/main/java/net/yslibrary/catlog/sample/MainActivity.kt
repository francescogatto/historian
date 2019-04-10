package net.francescogatto.catlog.sample

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.material.floatingactionbutton.FloatingActionButton
import timber.log.Timber
import java.util.concurrent.atomic.AtomicLong

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
            while (i < 100) {
                4 / 0
                Timber.i("test: %d", counter.getAndIncrement());
                Timber.tag("TEST").i("test: %d", counter.getAndIncrement())
                i++
                Timber.e(RuntimeException("sjndjf"), "Error");
            }
        }

        val exportButton = findViewById<Button>(R.id.export)
        exportButton.setOnClickListener {
            if (ContextCompat.checkSelfPermission(this@MainActivity, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                // export(this@MainActivity, App.getCatLog(this@MainActivity))
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
                //  export(this, App.getCatLog(this))
            }
        }
    }

    /*  fun export(context: Context, catLog: CatLog) {
          val dir = File(Environment.getExternalStorageDirectory(), "HistorianSample")
          if (!dir.exists()) {
              dir.mkdir()
          }
          val path = catLog.path()
          val exportPath = dir.path + File.separator + catLog.name()

          var fis: FileInputStream? = null
          var output: OutputStream? = null

          val dbFile = File(path)
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
