package com.example.linearlayout_sample;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.Toast;

public class MainActivity extends ActionBarActivity {
	// 選択中の音符の種類
	NoteType selectNoteType;

	HorizontalScrollView scoreScrollView;
	LinearLayout scoreLinearLayout;
	OnClickListener mOnLineClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			Context context = v.getContext();

			LinearLayout selectLine = (LinearLayout) v;
			LinearLayout measure = (LinearLayout) v.getParent();

			// 音符の総数を数える(16分音符が何個分あるか)
			int weightSum = 0;
			for (int i = 0; i < selectLine.getChildCount(); i++) {
				weightSum += ((LayoutParams) (selectLine.getChildAt(i).getLayoutParams())).weight;
			}

			// 音符が置けるかチェック
			float weight = selectNoteType.getWeight();
			if (weight > 16 - weightSum) {
				Toast.makeText(v.getContext(), "この音符はここには置けません", Toast.LENGTH_SHORT).show();
				return;
			}

			// すべてのlineに対して音符を追加
			for (int i = 0; i < measure.getChildCount(); i++) {
				// i番目のlineを取得
				LinearLayout line = (LinearLayout) measure.getChildAt(i);

				// 追加する音符を作成
				ImageView note = new ImageView(context);

				// 音符の大きさを指定
				LayoutParams params = new LayoutParams(0, LayoutParams.MATCH_PARENT);
				params.weight = weight;
				note.setLayoutParams(params);
				note.setOnClickListener(mOnNoteClickListener);

				// 選択したlineの音符には画像をつける
				if (line == v) {
					note.setImageResource(selectNoteType.getResourceId());
				}

				line.addView(note);
			}
			// weightSumの更新
			weightSum += weight;

			// 音符の総数が小節の最大数を超えたら小節を追加
			if (weightSum >= selectLine.getWeightSum()) {
				addMeasure(v);
			}
		}

	};
	OnClickListener mOnNoteClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			ImageView selectNote = (ImageView) v;
			LinearLayout selectLine = (LinearLayout) v.getParent();
			LinearLayout selectMeasure = (LinearLayout) selectLine.getParent();

			int indexOfSelectImage = selectLine.indexOfChild(selectNote);

			// クリックした音符からあとどれだけ音符が置けるかをチェック
			float weightRest = 0;
			for (int i = indexOfSelectImage; i < selectLine.getChildCount(); i++) {
				LayoutParams params = (LayoutParams) selectLine.getChildAt(i).getLayoutParams();
				weightRest += params.weight;
			}

			// 音符が置けるかチェック
			float weight = selectNoteType.getWeight();

			if (weight > weightRest) {
				Toast.makeText(v.getContext(), "この音符はここには置けません", Toast.LENGTH_SHORT).show();
				return;
			}

			// 選択中の音符の種類とクリックした音符の大きさを比較
			LayoutParams selectNoteParams = (LayoutParams) selectNote.getLayoutParams();
			float selectNoteParamsWeight = selectNoteParams.weight;
			int compare = Float.valueOf(weight).compareTo(selectNoteParamsWeight);

			for (int i = 0; i < selectMeasure.getChildCount(); i++) {
				LinearLayout line = (LinearLayout) selectMeasure.getChildAt(i);
				ImageView note = (ImageView) line.getChildAt(indexOfSelectImage);
				ImageView nextNote = (ImageView) line.getChildAt(indexOfSelectImage + 1);

				note.setImageDrawable(null);
				LayoutParams p = (LayoutParams)note.getLayoutParams();
				p.weight = weight;
				note.setLayoutParams(p);

				if (nextNote != null) {
					if (compare >= 1) {
						// 選択した音符が大きいとき
						System.out.println("selectNoteType > clickNote.weight");
						//

					} else if (compare <= -1) {
						// クリックした音符が大きいとき
						System.out.println("selectNoteType < clickNote.weight");
						// 今の位置の音符を分解
						System.out.println("selectNoteType.weight: " + weight + ", clickNote.weight: "
								+ selectNoteParamsWeight);
						breakDown(line, nextNote, (int) (selectNoteParamsWeight / weight), weight);
					}
				}
			}

			selectNote.setImageResource(selectNoteType.getResourceId());
		}

		public void breakDown(LinearLayout line, ImageView nextNote, int rate, float weight) {
			LayoutParams params = (LayoutParams) nextNote.getLayoutParams();
			params.weight = rate;
			nextNote.setLayoutParams(params);

			int pos = line.indexOfChild(nextNote);
			System.out.println("pos" + pos);
			for (int i = 1; i < rate; i++) {
				// 追加する音符を作成
				ImageView note = createImage(line.getContext(), rate);
				note.setImageResource(NoteType.valueOf(rate).getResourceId());
				line.addView(note, pos);
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		scoreScrollView = (HorizontalScrollView) findViewById(R.id.scoreScrollView);
		scoreLinearLayout = (LinearLayout) findViewById(R.id.scoreLinearLayout);
		LinearLayout firstMeasure = (LinearLayout) findViewById(R.id.firstMeasure);

		// 初めの小節にonClickを実装する
		setOnLineClickListener(firstMeasure, mOnLineClickListener);

		// 音符の初期設定(8分音符)
		selectNoteType = NoteType.EIGHTH;
	}

	public void onSelectNoteEighth(View v) {
		selectNoteType = NoteType.EIGHTH;
	}

	public void onSelectNoteQuarter(View v) {
		selectNoteType = NoteType.QUARTER;
	}

	public void addMeasure(View v) {
		final LinearLayout measure = createMeasure(v.getContext());
		scoreLinearLayout.addView(measure);

		new Handler().post(new Runnable() {

			@Override
			public void run() {
				scoreScrollView.smoothScrollBy(measure.getWidth(), 0);
			}
		});
	}

	public ImageView createImage(Context context, float weight) {
		// 追加する音符を作成
		ImageView note = new ImageView(context);

		// 音符の大きさを指定
		LayoutParams params = new LayoutParams(0, LayoutParams.MATCH_PARENT);
		params.weight = weight;
		note.setLayoutParams(params);
		note.setOnClickListener(mOnNoteClickListener);

		return note;
	}

	public LinearLayout createMeasure(Context context) {
		// measureのViewを作成
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		LinearLayout measure = (LinearLayout) inflater.inflate(R.layout.layout_measure, null);

		measure.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		setOnLineClickListener(measure, mOnLineClickListener);

		return measure;
	}

	private void setOnLineClickListener(LinearLayout measure, OnClickListener listener) {
		for (int i = 0; i < measure.getChildCount(); i++) {
			measure.getChildAt(i).setOnClickListener(listener);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
