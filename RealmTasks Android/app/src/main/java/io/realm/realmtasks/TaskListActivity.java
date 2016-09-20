/*
 * Copyright 2016 Realm Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.realm.realmtasks;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import io.realm.Realm;
import io.realm.realmtasks.list.RealmTasksViewHolder;
import io.realm.realmtasks.list.TaskListAdapter;
import io.realm.realmtasks.list.TouchHelper;
import io.realm.realmtasks.model.TaskList;
import io.realm.realmtasks.model.TaskListList;

public class TaskListActivity extends AppCompatActivity {

    private static final String TAG = "TaskListActivity";
    private Realm realm;
    private RecyclerView recyclerView;
    private TaskListAdapter adapter;
    private TouchHelper touchHelper;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_common_list);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    protected void onStart() {
        super.onStart();
        realm = Realm.getDefaultInstance();
        adapter = new TaskListAdapter(realm.where(TaskListList.class).findFirst().getItems());
        recyclerView.setAdapter(adapter);
        touchHelper = new TouchHelper(new Callback());
        touchHelper.attachToRecyclerView(recyclerView);
    }

    @Override
    protected void onStop() {
        touchHelper.attachToRecyclerView(null);
        recyclerView.setAdapter(null);
        realm.close();
        super.onStop();
    }

    private class Callback implements TouchHelper.Callback {
        @Override
        public void onMoved(RecyclerView recyclerView, RealmTasksViewHolder from, RealmTasksViewHolder to) {
            final int fromPosition = from.getAdapterPosition();
            final int toPosition = to.getAdapterPosition();
            adapter.onItemMoved(fromPosition, toPosition);
            adapter.notifyItemMoved(fromPosition, toPosition);
        }

        @Override
        public void onArchived(RealmTasksViewHolder viewHolder) {
            adapter.onItemArchived(viewHolder.getAdapterPosition());
            adapter.notifyDataSetChanged();
        }

        @Override
        public void onDismissed(RealmTasksViewHolder viewHolder) {
            final int position = viewHolder.getAdapterPosition();
            adapter.onItemDismissed(position);
            adapter.notifyItemRemoved(position);
        }

        @Override
        public boolean onClicked(RealmTasksViewHolder viewHolder) {
            final int position = viewHolder.getAdapterPosition();
            final TaskList taskList = adapter.getItems().get(position);
            final String id = taskList.getId();
            Log.d(TAG, "id: " + id);
            final Intent intent = new Intent(TaskListActivity.this, TaskActivity.class);
            intent.putExtra("id", taskList.getId());
            TaskListActivity.this.startActivity(intent);
            return true;
        }

        @Override
        public void onChanged(RealmTasksViewHolder viewHolder) {
            adapter.onItemChanged(viewHolder);
            adapter.notifyItemChanged(viewHolder.getAdapterPosition());
        }

        @Override
        public void onAdded() {
            adapter.onItemAdded();
            adapter.notifyItemInserted(0);
        }

        @Override
        public void onReverted(boolean shouldUpdateUI) {
            adapter.onItemReverted();
            if (shouldUpdateUI) {
                adapter.notifyItemRemoved(0);
            }
        }

        @Override
        public void onExited() {
            finish();
        }
    }
}