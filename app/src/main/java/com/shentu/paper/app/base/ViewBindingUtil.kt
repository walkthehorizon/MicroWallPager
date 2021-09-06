
/**
 *    date   : 2021/7/7 14:15
 *    author : mingming.li
 *    e-mail : mingming.li@ximalaya.com
 */

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewbinding.ViewBinding
import java.lang.reflect.ParameterizedType


@JvmName("inflateWithGeneric")
fun <VB : ViewBinding> Any.inflateBindingWithGeneric(layoutInflater: LayoutInflater): VB =
    withGenericBindingClass<VB>(this) { clazz ->
        clazz.getMethod("inflate", LayoutInflater::class.java).invoke(null, layoutInflater) as VB
    }

@JvmName("inflateWithGeneric")
fun <VB : ViewBinding> Any.inflateBindingWithGeneric(layoutInflater: LayoutInflater, parent: ViewGroup?, attachToParent: Boolean): VB =
    withGenericBindingClass<VB>(this) { clazz ->
        clazz.getMethod("inflate", LayoutInflater::class.java, ViewGroup::class.java, Boolean::class.java)
            .invoke(null, layoutInflater, parent, attachToParent) as VB
    }

@JvmName("inflateWithGeneric")
fun <VB : ViewBinding> Any.inflateBindingWithGeneric(parent: ViewGroup): VB =
    inflateBindingWithGeneric(LayoutInflater.from(parent.context), parent, false)

fun <VB : ViewBinding> Any.bindViewWithGeneric(view: View): VB =
    withGenericBindingClass<VB>(this) { clazz ->
        clazz.getMethod("bind", View::class.java).invoke(null, view) as VB
    }

private fun <VB : ViewBinding> withGenericBindingClass(any: Any, block: (Class<VB>) -> VB): VB {
    any.allParameterizedType.forEach { parameterizedType ->
        parameterizedType.actualTypeArguments.forEach {
            try {
                return block.invoke(it as Class<VB>)
            } catch (e: NoSuchMethodException) {
                e.printStackTrace()
            } catch (e: ClassCastException) {
                e.printStackTrace()
            }
        }
    }
    throw IllegalArgumentException("There is no generic of ViewBinding.")
}

private val Any.allParameterizedType: List<ParameterizedType>
    get() {
        val genericParameterizedType = mutableListOf<ParameterizedType>()
        var genericSuperclass = javaClass.genericSuperclass
        var superclass = javaClass.superclass
        while (superclass != null) {
            if (genericSuperclass is ParameterizedType) {
                genericParameterizedType.add(genericSuperclass)
            }
            genericSuperclass = superclass.genericSuperclass
            superclass = superclass.superclass
        }
        return genericParameterizedType
    }
