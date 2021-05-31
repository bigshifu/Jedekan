package com.example.papb_pa

import android.content.Intent
import android.view.WindowManager
import androidx.appcompat.app.ActionBar
import com.daimajia.androidanimations.library.Techniques
import com.viksaa.sssplash.lib.activity.AwesomeSplash
import com.viksaa.sssplash.lib.cnst.Flags
import com.viksaa.sssplash.lib.model.ConfigSplash


class Splash : AwesomeSplash() {
    override fun initSplash(configSplash: ConfigSplash?) {
        val actionBar: ActionBar? = supportActionBar
        actionBar?.hide()
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        //menambahkan background
        configSplash!!.backgroundColor = R.color.kuning
        configSplash.animCircularRevealDuration = 2000
        configSplash.revealFlagX = Flags.REVEAL_LEFT
        configSplash.revealFlagX = Flags.REVEAL_BOTTOM

        configSplash.logoSplash = R.drawable.gambarmenu
        configSplash.animLogoSplashDuration = 2000
        configSplash.animTitleTechnique = Techniques.FadeIn

        configSplash.titleSplash = "Jedekan Gambar"
        configSplash.titleTextColor=R.color.white
        configSplash.titleTextSize = 50f
        configSplash.animLogoSplashDuration = 2000
        configSplash.animTitleTechnique = Techniques.FlipInX
        configSplash.titleFont = "fonts/goodunicornregularrxev.ttf"
    }
    override fun animationsFinished() {
        startActivity(Intent(this@Splash, MainActivity::class.java))
    }

}