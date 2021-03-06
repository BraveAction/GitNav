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

package giuliolodi.gitnav.data

import giuliolodi.gitnav.data.api.ApiHelper
import giuliolodi.gitnav.data.prefs.PrefsHelper
import io.reactivex.Completable
import io.reactivex.Observable
import org.eclipse.egit.github.core.Repository
import org.eclipse.egit.github.core.User
import org.eclipse.egit.github.core.event.Event

/**
 * Created by giulio on 12/05/2017.
 */

interface DataManager : ApiHelper, PrefsHelper {

    /**
     * Tries to authenticate to GitHub API.
     * If the authentication is successful, it stores the authentication token in shared prefs,
     * along with username and profile picture.
     * @param username
     * @param password
     * @return Completable onNext if successful, onError otherwise
     */
    fun tryAuthentication(username: String, password: String): Completable

    /**
     * Get user from username.
     * @param username
     * @return Observable<User>
     */
    fun getUser(username: String): Observable<User>

    /**
     * Page events of logged user.
     * @param pageN
     * @param itemsPerPage
     * @return Observable<List<Event>>
     */
    fun pageEvents(pageN: Int, itemsPerPage: Int): Observable<List<Event>>

    /**
     * Page repositories of user (logged user if username is null).
     * @param username
     * @param pageN
     * @param itemsPerPage
     * @param filter
     * @return Observable<List<Repository>>
     */
    fun pageRepos(username: String?, pageN: Int, itemsPerPage: Int, filter: HashMap<String,String>?): Observable<List<Repository>>

    /**
     * Downloads trending repos (one at a time) from github's websites.
     * @param period (daily, weekly, monthly)
     * @return Observable<Repository>
     */
    fun getTrending(period: String): Observable<Repository>

    /**
     * Get user's starred repos (logged user if username is null).
     * @param username
     * @param pageN
     * @param itemsPerPage
     * @param filter
     */
    fun pageStarred(username: String?, pageN: Int, itemsPerPage: Int, filter: HashMap<String,String>?): Observable<List<Repository>>

    /**
     * Check if user is followed by logged user
     * @param username
     * @return Boolean
     */
    fun getFollowed(username: String): Observable<Boolean>

}