/*
 * MIT License
 *
 * Copyright (c) 2016 GLodi
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package giuliolodi.gitnav;


import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.eclipse.egit.github.core.Repository;
import org.eclipse.egit.github.core.service.RepositoryService;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import giuliolodi.gitnav.Adapters.RepoAdapter;

public class RepoFragment extends Fragment {

    List<Repository> repositoryList;
    RepoAdapter repoAdapter;

    @BindView (R.id.repo_recycler_view) RecyclerView recyclerView;
    @BindView(R.id.repo_progress_bar) ProgressBar progressBar;
    @BindView(R.id.repo_refresh) SwipeRefreshLayout swipeRefreshLayout;

    @BindString(R.string.network_error) String network_error;

    public Map FILTER_OPTION;

    /*
        In order to prevent a bug that shows multiple line dividers on top of each other, I inserted
        a variable that allows the creation of only one line.
    */
    public boolean PREVENT_MULTPLE_SEPARATION_LINE = true;

    /*
        Having decided to use a SwipeRefreshLayout, using both that and the ProgressBar would be redundant.
        For this reason, whenever the info is refreshed with a Swipe, the ProgressBar is hidden. This is
        handled with HIDE_PROGRESS_BAR.
    */
    public boolean HIDE_PROGRESS_BAR = true;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.repo_fragment, container, false);
        setHasOptionsMenu(true);

        ButterKnife.bind(this, v);

        // Created filter and set to "created"
        FILTER_OPTION = new HashMap();
        FILTER_OPTION.put("sort", "created");

        if (Constants.isNetworkAvailable(getContext()))
            new getRepositories().execute();
        else
            Toast.makeText(getContext(), network_error, Toast.LENGTH_LONG).show();

        // Set swipe color and listener. For some reason access through R.color doesn't work
        swipeRefreshLayout.setColorSchemeColors(Color.parseColor("#448AFF"));
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                HIDE_PROGRESS_BAR = false;
                if (Constants.isNetworkAvailable(getContext()))
                    new getRepositories().execute();
                else
                    Toast.makeText(getContext(), network_error, Toast.LENGTH_LONG).show();
            }
        });

        return v;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.repo_sort_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (Constants.isNetworkAvailable(getContext())) {
            switch (item.getItemId()) {
                case R.id.repo_sort_created:
                    item.setChecked(true);
                    FILTER_OPTION.put("sort", "created");
                    PREVENT_MULTPLE_SEPARATION_LINE = false;
                    new getRepositories().execute();
                    return true;
                case R.id.repo_sort_updated:
                    item.setChecked(true);
                    FILTER_OPTION.put("sort", "updated");
                    PREVENT_MULTPLE_SEPARATION_LINE = false;
                    new getRepositories().execute();
                    return true;
                case R.id.repo_sort_pushed:
                    item.setChecked(true);
                    FILTER_OPTION.put("sort", "pushed");
                    PREVENT_MULTPLE_SEPARATION_LINE = false;
                    new getRepositories().execute();
                    return true;
                case R.id.repo_sort_alphabetical:
                    item.setChecked(true);
                    FILTER_OPTION.put("sort", "full_name");
                    PREVENT_MULTPLE_SEPARATION_LINE = false;
                    new getRepositories().execute();
                    return true;
                default:
                    return super.onOptionsItemSelected(item);
            }
        }
        else {
            Toast.makeText(getContext(), network_error, Toast.LENGTH_LONG).show();
            return super.onOptionsItemSelected(item);
        }
    }

    class getRepositories extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            // Set ProgressBar visible
            if (HIDE_PROGRESS_BAR)
                progressBar.setVisibility(View.VISIBLE);

            // Set recycler view invisible
            recyclerView.setVisibility(View.INVISIBLE);
        }
        @Override
        protected String doInBackground(String... strings) {
            // Authenticate
            RepositoryService repositoryService = new RepositoryService();
            repositoryService.getClient().setOAuth2Token(Constants.getToken(getContext()));

            // Get the RepositoryList and sort it based on creation date
            try {
                repositoryList = repositoryService.getRepositories(FILTER_OPTION);
            } catch (IOException e) {e.printStackTrace();}
            return null;
        }
        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            HIDE_PROGRESS_BAR = true;

            // Change TitleBar
            ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Repositories");

            // Set ProgressBar invisible
            progressBar.setVisibility(View.GONE);

            // Set recycler view visible
            recyclerView.setVisibility(View.VISIBLE);

            // Stop refresh circle
            swipeRefreshLayout.setRefreshing(false);

            // Bind list to adapter
            repoAdapter = new RepoAdapter(repositoryList);

            // Set adapter on RecyclerView and notify it
            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
            if (PREVENT_MULTPLE_SEPARATION_LINE) {
                recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL_LIST));
            }
            PREVENT_MULTPLE_SEPARATION_LINE = true;
            recyclerView.setLayoutManager(mLayoutManager);
            recyclerView.setItemAnimator(new DefaultItemAnimator());
            recyclerView.setAdapter(repoAdapter);
            repoAdapter.notifyDataSetChanged();
        }
    }

}
