package yj.com.fileexplorer;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;


public class FileAdapter extends BaseAdapter {
    private String TAG = getClass().getSimpleName();

    static class ViewHolder {
        ImageView fileImageUpper;
        ImageView fileImageDown;
        TextView fileName;
        TextView fileCount;
        TextView fileModifyTime;
        TextView fileSize;
        FrameLayout fileOther;
    }


    private LayoutInflater layoutInflater;
    private List<FileItem> data;
    private ListView listView;
    private Context context;

    public FileAdapter(Context context, List<FileItem> data, ListView listView) {
        this.layoutInflater = LayoutInflater.from(context);
        this.data = data;
        this.listView = listView;
        this.context = context;
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public FileItem getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.file_explorer_item, null);
            viewHolder = new ViewHolder();
            viewHolder.fileName = (TextView) convertView.findViewById(R.id.file_name);
            viewHolder.fileCount = (TextView) convertView.findViewById(R.id.file_count);

            viewHolder.fileModifyTime = (TextView) convertView.findViewById(R.id.file_modified_time);

            viewHolder.fileSize = (TextView) convertView.findViewById(R.id.file_size);
            viewHolder.fileImageUpper = (ImageView) convertView.findViewById(R.id.file_image_upper);
            viewHolder.fileImageDown = (ImageView) convertView.findViewById(R.id.file_image_down);

            viewHolder.fileOther = (FrameLayout) convertView.findViewById(R.id.file_root_frameLayout);
            convertView.setTag(viewHolder);
        }

        viewHolder = (ViewHolder) convertView.getTag();


        FileItem item = getItem(position);

        int imageId = item.getImageId();
        if (imageId == R.drawable.ic_fso_type_folder || imageId == R.drawable.ic_sd_storage_black || imageId == R.drawable.ic_storage_black) {
            viewHolder.fileImageUpper.setVisibility(View.GONE);
        } else {
            viewHolder.fileImageUpper.setVisibility(View.VISIBLE);
        }
        viewHolder.fileImageDown.setImageResource(item.getImageId());
        viewHolder.fileName.setText(item.getName());
        viewHolder.fileCount.setText(item.getCount());

        String modifyTime = item.getModifyTime();
        String size = item.getSize();

        viewHolder.fileSize.setText(size);
        viewHolder.fileModifyTime.setText(modifyTime);


        //root目录的信息
        if ("".equals(size) && "".equals(modifyTime)) {
            //异常掉size和modifyTime
            viewHolder.fileSize.setVisibility(View.GONE);
            viewHolder.fileModifyTime.setVisibility(View.GONE);

            //frameLayout
            viewHolder.fileOther.setVisibility(View.VISIBLE);
            //显示容量
            ((TextView) viewHolder.fileOther.findViewById(R.id.file_root_volume)).setText(item.getOther());
        } else {
            viewHolder.fileOther.setVisibility(View.GONE);

            viewHolder.fileSize.setVisibility(View.VISIBLE);
            viewHolder.fileModifyTime.setVisibility(View.VISIBLE);

            updateBackground(position, convertView);
        }
        return convertView;
    }

    private void updateBackground(int position, View view) {
        int backgroundId;
        if (listView.isItemChecked(position)) {
            backgroundId = R.color.colorPrimaryLight;
        } else {
            backgroundId = android.R.color.transparent;
        }
        view.setBackground(this.context.getResources().getDrawable(backgroundId));
    }
}