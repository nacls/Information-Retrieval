package ir.ceit.search.view.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import ir.ceit.search.R;
import ir.ceit.search.model.Dictionary;

public class QueryActivity extends AppCompatActivity {
    private String query;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_query);
        Dictionary dictionary = (Dictionary) getIntent().getSerializableExtra("dictionary");
        query = "";
        EditText editTextQuery = (EditText) findViewById(R.id.edit_query);
        Button search = (Button) findViewById(R.id.search_button);

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                query = editTextQuery.getText().toString();
                if (query.isEmpty()) {
                    Toast.makeText(QueryActivity.this, "ابتدا پرسمان خود را وارد کنید", Toast.LENGTH_LONG).show();
                } else {
                    openResultListActivity();
                }
            }
        });
    }

    public void openResultListActivity() {
        //this opens the results page
        Intent intent = new Intent(this, ResultListActivity.class);
        intent.putExtra("message", query);
        startActivity(intent);
    }
}
