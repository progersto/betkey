package com.betkey.base

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.view.ViewCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.SingleObserver
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable

abstract class BaseFragment : Fragment() {

    protected val myLifecycleOwner = MyLifecycleOwner()
    private val compositeDisposable = CompositeDisposable()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        myLifecycleOwner.lifecycle.handleLifecycleEvent(Lifecycle.Event.ON_CREATE)

    }

    override fun onResume() {
        super.onResume()
        myLifecycleOwner.lifecycle.handleLifecycleEvent(Lifecycle.Event.ON_RESUME)
    }

    override fun onPause() {
        super.onPause()
        myLifecycleOwner.lifecycle.handleLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        myLifecycleOwner.lifecycle.handleLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    }

    fun popBackStack() {
        activity?.supportFragmentManager?.popBackStack()
    }

    fun clearStack() {
        activity?.supportFragmentManager?.also { fm ->
            for (i in 0 until fm.backStackEntryCount) {
                fm.popBackStack()
            }
        }
    }

    fun showFragment(fragment: BaseFragment, idContainer: Int, tag: String) {
        activity!!.supportFragmentManager
            .beginTransaction()
            .replace(idContainer, fragment)
            .addToBackStack(tag)
            .commit()
    }

    fun addFragment(fragment: BaseFragment, idContainer: Int, tag: String) {
        activity!!.supportFragmentManager
            .beginTransaction()
            .add(idContainer, fragment)
            .addToBackStack(tag)
            .commit()
    }

    protected fun replaceFragmentInIdContent(fragment: BaseFragment) {
        activity!!.supportFragmentManager
            .beginTransaction()
            .replace(android.R.id.content, fragment)
            .addToBackStack(null)
            .commit()
    }

    protected fun replaceFragmentWithSharedElement(
        fragment: BaseFragment,
        dressingRoomContainer: Int,
        tag: String,
        view: View
    ) {
        activity!!.supportFragmentManager.beginTransaction()
            .replace(dressingRoomContainer, fragment)
            .addToBackStack(tag)
            .addSharedElement(view, ViewCompat.getTransitionName(view)!!)
            .commit()
    }

    open fun requestPermissionsResult(requestCode: Int, permissions: Array<out String>, resultCodes: IntArray) {}

    open fun activityResult(requestCode: Int, resultCode: Int, data: Intent?) {}

    fun <T> subscribe(single: Single<T>, success: (T) -> Unit, error: ((Throwable) -> Unit)? = null) {
        compositeDisposable.add(single.observeOn(AndroidSchedulers.mainThread()).subscribe(success,
            {
                error?.invoke(it)
                Log.d("", "")
            }))
    }

    fun subscribe(single: Completable, success: () -> Unit, error: ((Throwable) -> Unit)? = null) {
        compositeDisposable.add(single.observeOn(AndroidSchedulers.mainThread()).subscribe(success,
            {
                error?.invoke(it)
                Log.d("", "")
            }))
    }
}

class MyLifecycleOwner : LifecycleOwner {

    private var mLifecycleRegisty = LifecycleRegistry(this)

    override fun getLifecycle() = mLifecycleRegisty
}

