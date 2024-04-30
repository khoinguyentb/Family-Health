package com.kan.dev.familyhealth.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.disposables.Disposable

abstract class BaseFragment<T : ViewBinding> : Fragment() {

    protected lateinit var binding: T
    private lateinit var callback: OnBackPressedCallback

    protected val compositeDisposable = CompositeDisposable()

    open fun handlerBackPressed(){}
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    protected fun addDispose(disposable: Disposable?) {
        disposable?.let {
            compositeDisposable.add(disposable)
        }

    }

    override fun onDestroy() {
        super.onDestroy()
//        callback.remove()

    }

    override fun onDestroyView() {
        super.onDestroyView()
        compositeDisposable.dispose()

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = getBinding(inflater, container)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
    }

    abstract fun getBinding(inflater: LayoutInflater, container: ViewGroup?): T

    abstract fun initView()
    fun replaceFullViewFragment(fragment: Fragment, addToBackStack: Boolean){
        (requireActivity()  as BaseActivity<*>).replaceBackStackFragment(fragment, android.R.id.content, addToBackStack)
    }
    open fun closeFragment(fragment: Fragment) {
        (requireActivity() as BaseActivity<*>).handleBackpress()
    }

    fun addAndRemoveCurrentFragment(currentFragment : Fragment, newFragment : Fragment, addToBackStack: Boolean = false) {
        val transaction = requireActivity().supportFragmentManager.beginTransaction()
        transaction.remove(currentFragment)
        transaction.add(android.R.id.content, newFragment)
        if (addToBackStack) {
            transaction.addToBackStack(null)
        }
        transaction.commit()
    }

    protected open fun hideKeyboard() {
        if (activity != null) {
            (activity as BaseActivity<*>?)?.hideKeyboard()
        }
    }

    protected open fun showKeyboard(view: View?) {
        (requireActivity() as BaseActivity<*>?)?.showKeyboard(view)
    }

    protected fun setColorStatusBar(idColor : Int){
        if(activity != null){
            (activity as BaseActivity<*>).window.statusBarColor = ContextCompat.getColor(requireContext(), idColor)
        }
    }

    companion object{
        var isGoToSetting = false
        fun <F : Fragment> newInstance(fragment: Class<F>, args: Bundle? = null): F {
            val f = fragment.newInstance()
            args?.let {
                f.arguments = it
            }
            return f
        }

    }
}
