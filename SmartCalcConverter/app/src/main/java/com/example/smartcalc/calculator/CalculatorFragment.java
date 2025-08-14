package com.example.smartcalc.calculator;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.smartcalc.R;
import com.example.smartcalc.engine.ExpressionEvaluator;

import java.util.Locale;

public class CalculatorFragment extends Fragment {
	private TextView tvExpression;
	private TextView tvResult;
	private boolean isSecond = false;

	@Nullable
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_calculator, container, false);
		tvExpression = view.findViewById(R.id.tvExpression);
		tvResult = view.findViewById(R.id.tvResult);

		setupButtons(view);
		return view;
	}

	private void setupButtons(View root) {
		int[] ids = new int[] {
			R.id.btnSecond, R.id.btnPi, R.id.btnE, R.id.btnBack,
			R.id.btnAc, R.id.btnOpenParen, R.id.btnCloseParen, R.id.btnDivide,
			R.id.btnSeven, R.id.btnEight, R.id.btnNine, R.id.btnMultiply,
			R.id.btnFour, R.id.btnFive, R.id.btnSix, R.id.btnMinus,
			R.id.btnOne, R.id.btnTwo, R.id.btnThree, R.id.btnPlus,
			R.id.btnZero, R.id.btnDot, R.id.btnPercent, R.id.btnEquals,
			R.id.btnSin, R.id.btnCos, R.id.btnTan, R.id.btnPow,
			R.id.btnSqrt, R.id.btnLn, R.id.btnLog, R.id.btnFact,
			R.id.btnRecip
		};

		for (int id : ids) {
			View v = root.findViewById(id);
			if (v instanceof Button) {
				v.setOnClickListener(this::onButtonClick);
			}
		}
	}

	private void onButtonClick(View v) {
		int id = v.getId();
		Button b = (Button) v;
		String txt = b.getText().toString();
		if (id == R.id.btnSecond) {
			isSecond = !isSecond;
			return;
		}
		if (id == R.id.btnAc) {
			tvExpression.setText("");
			tvResult.setText("0");
			return;
		}
		if (id == R.id.btnBack) {
			CharSequence cur = tvExpression.getText();
			if (!TextUtils.isEmpty(cur)) {
				String s = cur.toString();
				s = s.substring(0, s.length() - 1);
				tvExpression.setText(s);
				evaluate();
			}
			return;
		}

		if (id == R.id.btnEquals) {
			evaluate();
			return;
		}

		String toAppend = mapButtonToToken(id, txt);
		appendToken(toAppend);
		evaluate();
	}

	private String mapButtonToToken(int id, String label) {
		switch (id) {
			case R.id.btnPlus: return "+";
			case R.id.btnMinus: return "-";
			case R.id.btnMultiply: return "*";
			case R.id.btnDivide: return "/";
			case R.id.btnPercent: return "%";
			case R.id.btnDot: return ".";
			case R.id.btnPi: return "pi";
			case R.id.btnE: return "e";
			case R.id.btnOpenParen: return "(";
			case R.id.btnCloseParen: return ")";
			case R.id.btnPow: return "^";
			case R.id.btnSqrt: return "sqrt(";
			case R.id.btnLn: return "ln(";
			case R.id.btnLog: return "log(";
			case R.id.btnSin: return (isSecond ? "asin(" : "sin(");
			case R.id.btnCos: return (isSecond ? "acos(" : "cos(");
			case R.id.btnTan: return (isSecond ? "atan(" : "tan(");
			case R.id.btnFact: return "!";
			case R.id.btnRecip: return "inv("; // 1/x
			default: return label; // numbers
		}
	}

	private void appendToken(String token) {
		CharSequence cur = tvExpression.getText();
		String next = (cur == null ? "" : cur.toString()) + token;
		tvExpression.setText(next);
	}

	private void evaluate() {
		String expr = tvExpression.getText().toString();
		if (expr.trim().isEmpty()) {
			tvResult.setText("0");
			return;
		}
		try {
			double val = ExpressionEvaluator.evaluate(expr);
			String out = pretty(val);
			tvResult.setText(out);
		} catch (IllegalArgumentException ex) {
			tvResult.setText("");
		}
	}

	private String pretty(double v) {
		if (Math.abs(v - Math.rint(v)) < 1e-12) {
			return String.format(Locale.getDefault(), "%.0f", v);
		}
		return String.format(Locale.getDefault(), "%.10g", v);
	}
}