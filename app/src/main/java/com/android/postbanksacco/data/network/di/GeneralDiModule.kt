package com.cbkandroid.cbkapp.network.di

import android.content.Context
import cn.pedant.SweetAlert.SweetAlertDialog
import com.scottyab.rootbeer.RootBeer
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


/**
 * General di module
 * Tells Dagger this is a Dagger module
 * Install this module in Hilt-generated SingletonComponent
 * @constructor Create empty General di module
 */
@InstallIn(SingletonComponent::class)
@Module
class GeneralDiModule {

    /**
     * Provide root beer
     *
     * @param appContext
     * @return
     */
    @Provides
    @Singleton
    fun provideRootBeer(@ApplicationContext appContext: Context): RootBeer {
        return RootBeer(appContext)
    }

    /**
     * Provide sweet alert dialog
     *
     * @param appContext
     * @return
     */
    @Provides
    fun provideSweetAlertDialog(@ApplicationContext appContext: Context): SweetAlertDialog {
        val progressDialog = SweetAlertDialog(appContext, SweetAlertDialog.WARNING_TYPE)
        progressDialog.setCancelable(false)
        return SweetAlertDialog(appContext)
    }


}