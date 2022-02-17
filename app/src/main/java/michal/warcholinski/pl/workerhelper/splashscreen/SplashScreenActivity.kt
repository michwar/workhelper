package michal.warcholinski.pl.workerhelper.splashscreen

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import michal.warcholinski.pl.workerhelper.MainActivity

/**
 * Created by Michał Warcholiński on 2022-01-24.
 */
class SplashScreenActivity : AppCompatActivity() {

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		val intent = Intent(this, MainActivity::class.java)
		startActivity(intent)
		finish()
	}
}