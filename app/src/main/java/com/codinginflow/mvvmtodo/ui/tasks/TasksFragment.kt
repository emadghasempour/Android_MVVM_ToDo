package com.codinginflow.mvvmtodo.ui.tasks

import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.codinginflow.mvvmtodo.R
import com.codinginflow.mvvmtodo.data.SortOrder
import com.codinginflow.mvvmtodo.data.Task
import com.codinginflow.mvvmtodo.databinding.FragmentTaskBinding
import com.codinginflow.mvvmtodo.extensions.changed
import com.codinginflow.mvvmtodo.viewmodel.TaskViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_task.*
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@AndroidEntryPoint
class TasksFragment : Fragment(R.layout.fragment_task), TaskAdapter.OnItemClickListener {
    private val taskViewModel: TaskViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val binding = FragmentTaskBinding.bind(view)

        val taskAdapter = TaskAdapter(this)

        binding.apply {
            recyclerViewTasks.apply {
                adapter = taskAdapter
                layoutManager = LinearLayoutManager(requireContext())
                setHasFixedSize(true)
            }

            ItemTouchHelper(object :
                ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
                override fun onMove(
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder,
                    target: RecyclerView.ViewHolder
                ): Boolean {
                    return false
                }

                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                    val task = taskAdapter.currentList[viewHolder.adapterPosition]
                    taskViewModel.onTaskSwiped(task)
                }

            }).attachToRecyclerView(recycler_view_tasks)
        }

        taskViewModel.tasks.observe(viewLifecycleOwner) {
            taskAdapter.submitList(it)
        }

        viewLifecycleOwner.lifecycleScope.launch {
            taskViewModel.taskEventFlow.collect { event ->
                when (event) {
                    is TaskViewModel.TaskEvent.ShowUndoDeleteMessage -> Snackbar.make(
                        requireView(),
                        "Task Deleted",
                        Snackbar.LENGTH_LONG
                    ).setAction("Undo") {
                        taskViewModel.onUndoDeleteTask(event.task)
                    }.show()
                    is TaskViewModel.TaskEvent.NavigateToEditTaskScreen -> {
                        val action =
                            TasksFragmentDirections.actionTasksFragmentToAddEditTaskFragment(event.task)
                        findNavController().navigate(action)
                    }
                }
            }
        }

        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_fragment_task, menu)

        val searchItem = menu.findItem(R.id.action_search)
        val searchView = searchItem.actionView as SearchView

        searchView.changed {
            taskViewModel.searchValue.value = it
        }
        viewLifecycleOwner.lifecycleScope.launch {
            menu.findItem(R.id.action_hide_completed_tasks).isChecked =
                taskViewModel.preferencesFlow.first().hideCompleted
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_sort_by_name -> {
                Log.d("tag", "sort by name")
                taskViewModel.onSortOrderChanged(SortOrder.BY_NAME)
                true
            }

            R.id.action_sort_by_date_created -> {
                Log.d("tag", "sort by date")

                taskViewModel.onSortOrderChanged(SortOrder.BY_DATE)
                true
            }

            R.id.action_hide_completed_tasks -> {
                item.isChecked = !item.isChecked
                taskViewModel.onHideCompletedUpdate(item.isChecked)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onItemClick(task: Task) {
        taskViewModel.onTaskClicked(task = task)
    }

    override fun onCheckBoxClick(task: Task, isChecked: Boolean) {
        taskViewModel.onTaskCheckChanged(task, isChecked)
    }
}