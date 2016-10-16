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
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.vstechlab.easyfonts.EasyFonts;

import org.eclipse.egit.github.core.Repository;
import org.ocpsoft.prettytime.PrettyTime;

import java.text.DateFormat;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RepoAdapter extends RecyclerView.Adapter<RepoAdapter.MyViewHolder> {

    private List<Repository> repositoryList;

    public class MyViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.repo_list_name) TextView repo_list_name;
        @BindView(R.id.repo_list_description) TextView repo_list_description;
        @BindView(R.id.repo_list_language) TextView repo_list_language;
        @BindView(R.id.repo_list_forked) TextView repo_list_forked;
        @BindView(R.id.repo_list_star_number) TextView repo_list_star_number;
        @BindView(R.id.repo_list_date) TextView repo_list_date;

        @BindView(R.id.repo_code) ImageView repo_list_language_icon;

        public MyViewHolder(View view) {
            super(view);

            ButterKnife.bind(this, view);

            // Use easy fonts to set Typeface
            repo_list_name.setTypeface(EasyFonts.robotoRegular(view.getContext()));
            repo_list_description.setTypeface(EasyFonts.robotoRegular(view.getContext()));
            repo_list_language.setTypeface(EasyFonts.robotoRegular(view.getContext()));
            repo_list_forked.setTypeface(EasyFonts.robotoRegular(view.getContext()));
            repo_list_star_number.setTypeface(EasyFonts.robotoRegular(view.getContext()));
            repo_list_date.setTypeface(EasyFonts.robotoRegular(view.getContext()));
        }
    }

    public RepoAdapter(List<Repository> repositoryList) {
        this.repositoryList = repositoryList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.repo_row, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        // Create PrettyTime object
        PrettyTime p = new PrettyTime();

        // Get repo and parent repo (if available)
        Repository repo = repositoryList.get(position);
        Repository parent;

        // Set repo name
        holder.repo_list_name.setText(repo.getName());

        // Set repo description
        if (repo.getDescription() != null && !repo.getDescription().equals(""))
            holder.repo_list_description.setText(repo.getDescription());
        else
            holder.repo_list_description.setText("No description");

        // Set repo language
        if (repo.getLanguage() == null) {
            holder.repo_list_language.setVisibility(View.GONE);
            holder.repo_list_language_icon.setVisibility(View.GONE);
        }
        else
            holder.repo_list_language.setText(repo.getLanguage());

        // Set star repo number
        holder.repo_list_star_number.setText(Integer.toString(repo.getWatchers()));

        // Set repo date
        holder.repo_list_date.setText(p.format(repo.getCreatedAt()));

        // Check if is forked, then prints parent's info
        if (repo.isFork() && repo.getParent() != null) {
            parent = repo.getParent();
            holder.repo_list_forked.setText(parent.getName());
        }
        else {
            holder.repo_list_forked.setVisibility(View.GONE);
        }


    }

    @Override
    public int getItemCount() {
        return repositoryList.size();
    }

}
