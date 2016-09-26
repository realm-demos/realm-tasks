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

import io.realm.Realm;
import io.realm.SyncConfiguration;
import io.realm.User;
import io.realm.realmtasks.model.TaskList;
import io.realm.realmtasks.model.TaskListList;

public class UserManager {

    // Configure Realm for the current active user
    public static void setActiveUser(User user) {
        SyncConfiguration defaultConfig = new SyncConfiguration.Builder(user, RealmTasksApplication.REALM_URL)
                .initialData(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        if (realm.isEmpty() || realm.where(TaskListList.class).count() == 0) {
                            final TaskListList taskListList = realm.createObject(TaskListList.class, 0);
                            final TaskList taskList = new TaskList();
                            taskList.setId(RealmTasksApplication.DEFAULT_LIST_ID);
                            taskList.setText(RealmTasksApplication.DEFAULT_LIST_NAME);
                            taskListList.getItems().add(taskList);
                        }
                    }
                })
                .build();
        Realm.setDefaultConfiguration(defaultConfig);
    }
}
