package com.example.smartcalc.engine;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public final class ExpressionEvaluator {
	private ExpressionEvaluator() {}

	public static double evaluate(String expression) throws IllegalArgumentException {
		if (expression == null) throw new IllegalArgumentException("null expr");
		List<Token> tokens = tokenize(expression);
		List<Token> rpn = toRpn(tokens);
		return evalRpn(rpn);
	}

	private enum Type { NUMBER, OP, LPAREN, RPAREN, FUNC, FACT, CONST }

	private static class Token {
		Type type;
		String text;
		double value;
		Token(Type type, String text) { this.type = type; this.text = text; }
		Token(double value) { this.type = Type.NUMBER; this.value = value; this.text = Double.toString(value); }
	}

	private static final Map<String, Integer> OP_PREC = new HashMap<>();
	private static final Map<String, Boolean> OP_RIGHT = new HashMap<>();
	static {
		OP_PREC.put("+", 1); OP_PREC.put("-", 1);
		OP_PREC.put("*", 2); OP_PREC.put("/", 2); OP_PREC.put("%", 2);
		OP_PREC.put("^", 3);
		OP_RIGHT.put("^", true);
	}

	private static boolean isOp(String s) { return OP_PREC.containsKey(s); }
	private static int prec(String s) { return OP_PREC.get(s); }
	private static boolean rightAssoc(String s) { return OP_RIGHT.getOrDefault(s, false); }

	private static List<Token> tokenize(String s) {
		List<Token> out = new ArrayList<>();
		int i = 0;
		while (i < s.length()) {
			char c = s.charAt(i);
			if (Character.isWhitespace(c)) { i++; continue; }
			if (Character.isDigit(c) || (c == '.' && i + 1 < s.length() && Character.isDigit(s.charAt(i+1)))) {
				int j = i + 1;
				while (j < s.length() && (Character.isDigit(s.charAt(j)) || s.charAt(j) == '.')) j++;
				double v = Double.parseDouble(s.substring(i, j));
				out.add(new Token(v));
				i = j;
				continue;
			}
			if (c == '(') { out.add(new Token(Type.LPAREN, "(")); i++; continue; }
			if (c == ')') { out.add(new Token(Type.RPAREN, ")")); i++; continue; }
			if (c == '!') { out.add(new Token(Type.FACT, "!")); i++; continue; }
			if ("+-*/%^".indexOf(c) >= 0) { out.add(new Token(Type.OP, Character.toString(c))); i++; continue; }

			// functions and consts
			if (Character.isLetter(c)) {
				int j = i + 1;
				while (j < s.length() && Character.isLetter(s.charAt(j))) j++;
				String name = s.substring(i, j).toLowerCase(Locale.US);
				if (name.equals("pi") || name.equals("e")) {
					out.add(new Token(Type.CONST, name));
				} else {
					out.add(new Token(Type.FUNC, name));
				}
				i = j;
				continue;
			}
			throw new IllegalArgumentException("Unexpected char: " + c);
		}
		return out;
	}

	private static List<Token> toRpn(List<Token> tokens) {
		List<Token> output = new ArrayList<>();
		Deque<Token> stack = new ArrayDeque<>();
		Token prev = null;
		for (Token t : tokens) {
			switch (t.type) {
				case NUMBER:
				case CONST:
					output.add(t);
					break;
				case FUNC:
					stack.push(t);
					break;
				case OP:
					String op = t.text;
					// handle unary minus
					if (op.equals("-") && (prev == null || prev.type == Type.OP || prev.type == Type.LPAREN)) {
						// represent unary minus as function neg(x)
						stack.push(new Token(Type.FUNC, "neg"));
						break;
					}
					while (!stack.isEmpty() && stack.peek().type == Type.OP) {
						String top = stack.peek().text;
						if ((!rightAssoc(op) && prec(op) <= prec(top)) || (rightAssoc(op) && prec(op) < prec(top))) {
							output.add(stack.pop());
						} else break;
					}
					stack.push(t);
					break;
				case LPAREN:
					stack.push(t);
					break;
				case RPAREN:
					while (!stack.isEmpty() && stack.peek().type != Type.LPAREN) {
						output.add(stack.pop());
					}
					if (stack.isEmpty()) throw new IllegalArgumentException("Mismatched parens");
					stack.pop(); // pop LPAREN
					// if top is a function, pop it too
					if (!stack.isEmpty() && stack.peek().type == Type.FUNC) output.add(stack.pop());
					break;
				case FACT:
					// factorial is postfix - output as op
					output.add(t);
					break;
			}
			prev = t;
		}
		while (!stack.isEmpty()) {
			Token t = stack.pop();
			if (t.type == Type.LPAREN || t.type == Type.RPAREN) throw new IllegalArgumentException("Mismatched parens");
			output.add(t);
		}
		return output;
	}

	private static double evalRpn(List<Token> rpn) {
		Deque<Double> stack = new ArrayDeque<>();
		for (Token t : rpn) {
			switch (t.type) {
				case NUMBER:
					stack.push(t.value);
					break;
				case CONST:
					if (t.text.equals("pi")) stack.push(Math.PI);
					else stack.push(Math.E);
					break;
				case OP:
					double b = pop(stack);
					double a = pop(stack);
					switch (t.text) {
						case "+": stack.push(a + b); break;
						case "-": stack.push(a - b); break;
						case "*": stack.push(a * b); break;
						case "/": stack.push(a / b); break;
						case "%": stack.push(a % b); break;
						case "^": stack.push(Math.pow(a, b)); break;
					}
					break;
				case FUNC:
					double x = pop(stack);
					switch (t.text) {
						case "sin": stack.push(Math.sin(x)); break;
						case "cos": stack.push(Math.cos(x)); break;
						case "tan": stack.push(Math.tan(x)); break;
						case "asin": stack.push(Math.asin(x)); break;
						case "acos": stack.push(Math.acos(x)); break;
						case "atan": stack.push(Math.atan(x)); break;
						case "ln": stack.push(Math.log(x)); break;
						case "log": stack.push(Math.log10(x)); break;
						case "sqrt": stack.push(Math.sqrt(x)); break;
						case "inv": stack.push(1.0 / x); break;
						case "neg": stack.push(-x); break;
						default: throw new IllegalArgumentException("Unknown func: " + t.text);
					}
					break;
				case FACT:
					double n = pop(stack);
					stack.push(factorial(n));
					break;
			}
		}
		if (stack.size() != 1) throw new IllegalArgumentException("Bad expression");
		return stack.pop();
	}

	private static double pop(Deque<Double> s) {
		if (s.isEmpty()) throw new IllegalArgumentException("Stack underflow");
		return s.pop();
	}

	private static double factorial(double x) {
		if (x < 0) throw new IllegalArgumentException("negative factorial");
		if (Math.abs(x - Math.rint(x)) < 1e-12) {
			int n = (int)Math.rint(x);
			double r = 1.0;
			for (int i = 2; i <= n; i++) r *= i;
			return r;
		}
		// gamma approximation for non-integers (using Euler's reflection or Stirling)
		return gamma(x + 1.0);
	}

	private static double gamma(double z) {
		// Lanczos approximation
		double[] p = {
			676.5203681218851,
			-1259.1392167224028,
			771.32342877765313,
			-176.61502916214059,
			12.507343278686905,
			-0.13857109526572012,
			9.9843695780195716e-6,
			1.5056327351493116e-7
		};
		int g = 7;
		if (z < 0.5) {
			return Math.PI / (Math.sin(Math.PI * z) * gamma(1 - z));
		}
		z -= 1;
		double x = 0.99999999999980993;
		for (int i = 0; i < p.length; i++) {
			x += p[i] / (z + i + 1);
		}
		double t = z + g + 0.5;
		return Math.sqrt(2 * Math.PI) * Math.pow(t, z + 0.5) * Math.exp(-t) * x;
	}
}