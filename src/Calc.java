/*
Возведение числа в степень:
http://proglang.su/java/numbers-pow
 */

import java.util.Scanner;

import static java.lang.Double.*;

public class Calc {

    private static final String VERSION = "1.8";

    private static final String CMD_HELP = "?";
    private static final String CMD_END = "end";
    private static final String CMD_SW_TRACE = "#";    //команда вкл/откл трассировки вычислений
    private static final String CMD_TEST = "test";     //команда ввода тестовой формулы

    //формула для тестирования
    private static final String[][] FORMULA_TESTS = {
            {"3+6^2*3+((2+(-1*18+3)+(3+64/8-2)^3)+7)+25^3", "16459"},
            {"78/6*9+(((((9*6-6)+(-6+3/2+1))+1)+2*3)*2)+6/3-5", "217"},
            {"7+8+9*5+9+(8+66*5+1)", "408"},
            {"14+(-96/8)+(8+(6/3+5)+9)", "26"},
            {"12-3*(8+33*2)*(85/5-3)", "-3096"},
            {"44/8+9-(7*(7-5*9+2)-7)+14", "287.5"},
            {"85*9-65*2+5-(96/6+3)/(7*6-56)", "641.3571428571429"},
            {"9+3*5-(9/6+3*2+4*8+1)+(((1*2+1)+8*6/3+1)-96)", "-92.5"},
            {"(9+6+33*6+1*24/3 )", "221"},
            {"(((5*3-2)-(5+66)*(-1))+21/7+6*88)+323-55*2", "828"},
            {"-66+5*6-32*((45/6+1)-(96-65*2)+1)*2", "-2820"},
            {"(85*3-6)*(-1)/8+526-25*41", "-530.125"},
            {"32-36*(7/6*2+22)*(8-6/3*22)", "31568"},
            {"9+88+(((4*7+66/3+77)-22)*2)+77+5^2+55", "464.0"},
            {"768+(54+56*7+22*(54-32*3+77)-78-98*2)/2", "1239.0"},
    };


    //    private static final boolean TRACE_ON = false;    //по умолчанию не показывать трассировку вычислений
    private static final boolean TRACE_ON = true;       //по умолчанию показывать трассировку вычислений

    private boolean trace;

    private String[] arr;

    //=========== ОСНОВНОЙ БЛОК ==========
    public void go(){
        Scanner sc = new Scanner(System.in);

        System.out.println("КАЛЬКУЛЯТОР " + VERSION);
        System.out.println();
        boolean end = false;

        trace = TRACE_ON;       //включение трассировки

        do {

            System.out.println("? помощь, # трассировка вкл/откл, end - выход ");
            System.out.println("..............................................");
            System.out.print  ("Введите формулу: ");
            String str = sc.nextLine();

            //команды
            switch (str.toLowerCase().trim())
            {
                case CMD_END:
                    end = true;
                    continue;
                case CMD_HELP:
                    printHelp();
                    continue;
                case CMD_SW_TRACE:
                    trace = !trace;
                    String traceState = (trace) ? "Трассировка включена" : "Трассировка отключена";
                    System.out.println(traceState);
                    System.out.println();
                    continue;
                default:
                    String[] test = getTest(str);
                    if(test != null) {
                        System.out.printf("тестовая формула[%s]: %s   \n", test[0], test[1]);
                        System.out.println("ожидаемый ответ: " + test[2]);
                        str = test[1];
                    }
                    break;
            }

//          arr = str.split(" ");   //ввод через split
            arr = Formula.formulaToArr(str);   //переводим строку-формулу в массив и с пробелами, и без

            //формула ок? - проводим рассчет
            if(Formula.checkFormulaCorrect(arr))
            {
                String formula = MyArr.arrToStr(arr);

                try {
                    arr = calculate(arr);
                    System.out.println(".........");
                    System.out.println(formula + " = " + arr[0]);
                }
                catch (MyException ex)
                {
                    System.out.println(ex.getMessage());
                    if (!trace)
                    {
                        System.out.println("Для получения подробностей, включите трассировку и повторите ввод формулы");
                    }
                }
            }
            //
            System.out.println();

        }while(!end);

        System.out.println();
        System.out.println("JAVA A01 \"ШАГ\", Запорожье 2021");
        System.out.println("Перцух Алексей");
    }
    //====================

    //точка входа в скобки
    private int startPos(String arr[]) {
        int pos = 0;
        for (int i = 0; i < arr.length; i++) {
            if(Formula.isBracketOpen(arr[i])) {
                pos = i;
            }

            if(Formula.isBracketClose(arr[i])) {
               break;
            }
        }
        return pos;
    }


    private String[] delBracket(String[] arr, int pos )
    {
        //удаляем лишние скобки
        while(pos < arr.length - 2 )
        {
            if(Formula.isBracketOpen(arr[pos]) && Formula.isBracketClose(arr[pos + 2]))
            {
                arr = MyArr.del(arr, pos + 2);
                arr = MyArr.del(arr, pos);

                if(trace) {
                    MyArr.printlnArr(arr);
                }

//                return delBracket(arr, pos);
//                System.out.println("bracket");
            }
            else {
                pos++;
            }
        }
        return arr;
    }

    private String[] calculate(String[] arr, String[] arrOperators, int pos) throws MyException {
        //обрабатываем операции, пока не наткнемся на закр. скобку или не дойдем до конца массива
        while (pos < arr.length && !Formula.isBracketClose(arr[pos]))
        {
            //знак операции?
            if (Formula.isMathOperator(arrOperators, arr[pos])) {
                //массив уже предварительно проверен на корректность, а потому pos сюда придет ок, но на всякий случай все равно pos проверим на выход за массив
                if(pos - 1 < 0 || pos + 1 >= arr.length) {
                    throw new MyException("Ошибка: выход за размеры массива");
                }

                String strA = arr[pos - 1];
                String strB = arr[pos + 1];

                double a;
                double b;

                //ошибки быть не должно- массив предварительно проверяется на корректность,
                //но на всякий случай проверим преобразование на ошибку
                if(Formula.isDouble(strA)) {
                    a = Double.parseDouble(strA);
                }
                else {
                    String message = String.format("Ошибка: не удалось преобразовать строку \"%s\" в double", strA);
                    throw new MyException(message);
                }

                if(Formula.isDouble(strB)) {
                    b = Double.parseDouble(strB);
                }
                else {
                    String message = String.format("Ошибка: не удалось преобразовать строку \"%s\" в double", strB);
                    throw new MyException(message);
                }

                double c = oper(a, b, arr[pos]);

                //было деление на ноль?
                if (c == POSITIVE_INFINITY || c == NEGATIVE_INFINITY) {
                    String message = String.format("Ошибка: деление на ноль %s %s %s ", a, arr[pos], b);
                    throw new MyException(message);
                }

                //ошибка возведения в степень, напр. -7.5 ^ 1.4 = NaN
                if (Double.isNaN(c)) {
                    String message = String.format("Ошибка: %s %s %s = NaN  ", a, arr[pos], b);
                    throw new MyException(message);
                }

                String strVal = Double.toString(c);

                arr = MyArr.del(arr, pos - 1, 3);
                arr = MyArr.insert(arr, strVal, pos - 1);

                //если включена трассировка- распечатываем
                if (trace) {
                    MyArr.printlnArr(arr);
                }
//                return calculate(arr, arrOperators, pos);
            }
            else {
                pos++;
            }
        }

        return arr;
    }


    private String[] calculate(String[] arr) throws MyException {
        if(trace) {
            MyArr.printlnArr(arr);
        }

        int oldLength;

        //обсчитываем, пока в массиве не останется последняя строка- это результат
        while(arr.length > 1)
        {
            oldLength = arr.length;
            arr = delBracket(arr, 0);
            //обрабатываем операции по приоритетам
            for (int i = 0; i < Formula.operators.length; i++) {
                arr = calculate(arr, Formula.operators[i], startPos(arr));
            }

             //обрубаем вечный цикл, если что-то пойдет не так
            if(oldLength == arr.length)
            {
                String message = "!!! Вычисление не завершено - что-то пошло не так. Необработанная часть формулы: " + MyArr.arrToStr(arr);
                throw new MyException(message);
            }
        }
        return arr;
    }

    public static double oper(double a, double b, String cmd) {
        switch (cmd){
            case "+":
                return a + b;
            case "-":
                return a - b;
            case "*":
                return a * b;
            case ":":
            case "/":
                return a / b;
            case "^":
                return Math.pow(a, b);
        }
        return 0;
    }

    private static void printHelp(){
        System.out.println("----");
        System.out.println("• Формулы можно вводить без пробелов");
        System.out.println("• Поддерживаются скобки любой вложеной глубины, напр.: (3+((1+2)+(5*4-5^2)+1)+2)*2  ");
        System.out.printf ("• Команда для ввода большой тестовой формулы: %s или %s0 ... %s%d   \n", CMD_TEST, CMD_TEST, CMD_TEST, FORMULA_TESTS.length - 1);
        System.out.println("• Поддерживаются операции: " + MyArr.arrToStr(Formula.operators," , ") );
        System.out.println("• Трассировка показывает последовательность выполнения расчетов");
        System.out.println("----");
        System.out.println("https://github.com/AlexeyPertsukh/hw12-java-exceptions-calc");
        System.out.println();
    }

    //тестовая формула из массива по запросу test или test0 .... testXXX
    private String[] getTest(String str) {

        if(str.length() < CMD_TEST.length()) {
            return null;
        }

        String key= str.substring(0,CMD_TEST.length());

        if(!Formula.cmpStr(CMD_TEST, key))
        {
            return null;
        }

        if(str.length() == CMD_TEST.length()) {
            return new String[] {"0", FORMULA_TESTS[0][0], FORMULA_TESTS[0][1]};
        }

        String strNum = str.substring(CMD_TEST.length());

        if(!Formula.isDouble(strNum)) {
            return null;
        }

        int num = Integer.parseInt(strNum);

        if(num < 0 || num >= FORMULA_TESTS.length) {
            return null;
        }

        return new String[] {strNum, FORMULA_TESTS[num][0], FORMULA_TESTS[num][1]};

    }

}
