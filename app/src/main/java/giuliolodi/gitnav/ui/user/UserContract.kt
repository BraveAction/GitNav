/*
 * Copyright 2017 GLodi
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

package giuliolodi.gitnav.ui.user

import giuliolodi.gitnav.di.scope.PerActivity
import giuliolodi.gitnav.ui.base.BaseContract
import org.eclipse.egit.github.core.Repository
import org.eclipse.egit.github.core.User

/**
 * Created by giulio on 19/05/2017.
 */

interface UserContract {

    interface View : BaseContract.View {

        fun showUser(mapUserFollowed: Map<User, Boolean>)

        fun showLoading()

        fun hideLoading()

        fun showError(error: String)

        fun showUserRepos(repoList: List<Repository>)

        fun showUserFollowers(userList: List<User>)

        fun showUserFollowing(userList: List<User>)

    }

    @PerActivity
    interface Presenter<V: UserContract.View> : BaseContract.Presenter<V> {

        fun subscribe(username: String)

        fun getRepos(username: String, pageN: Int, itemsPerPage: Int, filter: HashMap<String,String>)

        fun getFollowers(username: String, pageN: Int, itemsPerPage: Int, filter: HashMap<String,String>)

        fun getFollowing(username: String, pageN: Int, itemsPerPage: Int, filter: HashMap<String,String>)

        fun followUser(username: String)

        fun unFollowUser(username: String)

    }

}