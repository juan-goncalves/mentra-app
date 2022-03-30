package me.juangoncalves.mentra.platform.locale

import java.util.*
import javax.inject.Inject

class DefaultLocaleProvider @Inject constructor() : LocaleProvider {

    override fun getDefault(): Locale = Locale.getDefault()
}