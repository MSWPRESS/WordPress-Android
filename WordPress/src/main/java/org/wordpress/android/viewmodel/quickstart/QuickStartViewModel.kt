package org.wordpress.android.viewmodel.quickstart

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import org.wordpress.android.fluxc.store.QuickStartStore
import org.wordpress.android.fluxc.store.QuickStartStore.QuickStartTask
import org.wordpress.android.ui.quickstart.QuickStartDetailModel
import javax.inject.Inject

class QuickStartViewModel @Inject constructor(private val quickStartStore: QuickStartStore) : ViewModel() {
    private val tasks = MutableLiveData<List<QuickStartDetailModel>>()
    val quickStartTasks: LiveData<List<QuickStartDetailModel>>
        get() = tasks

    private var isStarted = false

    fun start(siteId: Long) {
        if (isStarted) {
            return
        }

        refreshTaskCompletionStatuses(siteId)

        isStarted = true
    }

    private fun refreshTaskCompletionStatuses(siteId: Long) {
        val list = ArrayList<QuickStartDetailModel>()
        QuickStartTask.values().forEach {
            // CREATE_SITE task is completed by default
            list.add(QuickStartDetailModel(it,
                    if (it == QuickStartTask.CREATE_SITE) true else quickStartStore.hasDoneTask(siteId, it)))

        }
        tasks.postValue(list)
    }

    fun setDoneTask(siteId: Long, task: QuickStartTask, isCompleted: Boolean) {
        quickStartStore.setDoneTask(siteId, task, isCompleted)
        refreshTaskCompletionStatuses(siteId)
    }

    fun skipAllTasks(siteId: Long) {
        QuickStartTask.values().forEach { quickStartStore.setDoneTask(siteId, it, true) }
        refreshTaskCompletionStatuses(siteId)
    }
}
