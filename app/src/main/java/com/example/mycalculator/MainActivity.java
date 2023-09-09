package com.example.mycalculator;

import static android.os.Build.VERSION_CODES.R;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayDeque;
import java.util.Deque;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView solutionTextView;
    private TextView resultTextView;
    private StringBuilder expressionBuilder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        solutionTextView = findViewById(R.id.solution_tv);
        resultTextView = findViewById(R.id.result_tv);
        expressionBuilder = new StringBuilder();

        // Set click listeners for all buttons
        int[] buttonIds = {
                R.id.button_AC, R.id.button_C, R.id.open_brackete, R.id.close_brackete,
                R.id.button_0, R.id.button_1, R.id.button_2, R.id.button_3,
                R.id.button_4, R.id.button_5, R.id.button_6, R.id.button_7,
                R.id.button_8, R.id.button_9, R.id.add, R.id.subt,
                R.id.multiply, R.id.divide, R.id.button_dot, R.id.equal
        };

        for (int id : buttonIds) {
            findViewById(id).setOnClickListener(this);
        }
    }


    @Override
    public void onClick(View view) {
        Button button = (Button) view;
        String buttonText = button.getText().toString();

        switch (view.getId()) {
            case R.id.button_AC:
                expressionBuilder.setLength(0);
                solutionTextView.setText("");
                resultTextView.setText("");
                break;

            case R.id.button_C:
                if (expressionBuilder.length() > 0) {
                    expressionBuilder.deleteCharAt(expressionBuilder.length() - 1);
                }
                break;

            case R.id.equal:
                try {
                    String expression = expressionBuilder.toString();
                    double result = evaluateExpression(expression);
                    solutionTextView.setText(expression); // Display the expression
                    resultTextView.setText(String.valueOf(result));
                } catch (Exception e) {
                    solutionTextView.setText("Error");
                    resultTextView.setText("");
                }
                break;

            default:
                expressionBuilder.append(buttonText);
                solutionTextView.append(buttonText); // Append the clicked button text to the solution area
                break;
        }
    }




    private double evaluateExpression(String expression) {
        try {
            // Evaluate the expression using the Shunting Yard algorithm
            Deque<Double> values = new ArrayDeque<>();
            Deque<Character> operators = new ArrayDeque<>();

            for (int i = 0; i < expression.length(); i++) {
                char c = expression.charAt(i);

                if (Character.isDigit(c) || (c == '.' && i < expression.length() - 1 && Character.isDigit(expression.charAt(i + 1)))) {
                    StringBuilder numberBuilder = new StringBuilder();
                    while (i < expression.length() && (Character.isDigit(expression.charAt(i)) || expression.charAt(i) == '.')) {
                        numberBuilder.append(expression.charAt(i));
                        i++;
                    }
                    i--;

                    values.push(Double.parseDouble(numberBuilder.toString()));
                } else if (c == '(') {
                    operators.push(c);
                } else if (c == ')') {
                    while (!operators.isEmpty() && operators.peek() != '(') {
                        performOperation(values, operators);
                    }
                    operators.pop();
                } else if (isOperator(c)) {
                    while (!operators.isEmpty() && getPrecedence(c) <= getPrecedence(operators.peek())) {
                        performOperation(values, operators);
                    }
                    operators.push(c);
                }
            }

            while (!operators.isEmpty()) {
                performOperation(values, operators);
            }

            if (values.size() == 1) {
                return values.pop();
            } else {
                throw new RuntimeException("Invalid expression");
            }
        } catch (Exception e) {
            throw new RuntimeException("Invalid expression");
        }
    }

    private boolean isOperator(char c) {
        return c == '+' || c == '-' || c == '*' || c == '/';
    }

    private int getPrecedence(char operator) {
        if (operator == '+' || operator == '-') {
            return 1;
        } else if (operator == '*' || operator == '/') {
            return 2;
        }
        return 0;
    }

    private void performOperation(Deque<Double> values, Deque<Character> operators) {
        char operator = operators.pop();
        double operand2 = values.pop();
        double operand1 = values.pop();

        double result;
        switch (operator) {
            case '+':
                result = operand1 + operand2;
                break;
            case '-':
                result = operand1 - operand2;
                break;
            case '*':
                result = operand1 * operand2;
                break;
            case '/':
                if (operand2 == 0) {
                    throw new ArithmeticException("Division by zero");
                }
                result = operand1 / operand2;
                break;
            default:
                throw new RuntimeException("Invalid operator");
        }

        values.push(result);
    }
}
