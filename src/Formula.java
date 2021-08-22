public class Formula {

    private static final String MSG_ERR = "Ошибка в формуле:";
    private static final String MSG_INVALID_REC = "недопустимая запись";
    private static final String MSG_ERR_SIZE = MSG_ERR + " формула не введена";
    private static final String MSG_TOO_FEW = MSG_ERR + " недостаточно данных в формуле";

    private static final String STR_FORMAT_INVALID_REC = MSG_ERR + " " + MSG_INVALID_REC + " \"%s%s\"  \n";
    private static final String STR_FORMAT_TWO_NUM = MSG_ERR + " два числа подряд: %s %s  \n";
    private static final String STR_FORMAT_MISS_BRACK = MSG_ERR + " не хватает скобок \"%s\" \n";

    private static final String BRACKET_OPEN = "(";
    private static final String BRACKET_CLOSE = ")";

    //приоритеты операций
    private static final String[] operSuper = {"^"};                //супер приоритет операций
    private static final String[] operHi = {"*", ":", "/"};         //высокий приоритет операций
    private static final String[] operLow = {"+", "-"};             //низкий приоритет операций

    public static final String[][] operators = {operSuper, operHi, operLow};

    private Formula() {
    }


    //разбиваем введенную строку на массив строк, где каждая строка - мат.оператор, скобка или другая послед. символов
    //задача - вводить формулу хоть с пробелами, хоть без
    public static String[] formulaToArr(String str) {
        String[] arr = new String[0];
        //удаляем все пробелы
        str = str.replaceAll("\\s+", "");

        char ch;
        String strNum = "";
        for (int i = 0; i < str.length(); i++) {

            ch = str.charAt(i);

            //скобки
            if (Formula.isBracket(ch)) {
                if (strNum.length() > 0) {
                    arr = MyArr.add(arr, strNum);
                    strNum = "";
                }
                arr = MyArr.add(arr, ch);
                continue;
            }

            // знак минус- много вариантов для интерпретации
            if (ch == '-') {
                if (str.length() > i + 1) {

                    //после минуса стоит скобка, все ок, это просто знак операции. какая скобка, не важно- ошибки ищем в другом месте
                    if(Formula.isBracket(str.charAt(i + 1))) {
//                        System.out.println("@1");
                        if (strNum.length() > 0) {
                            arr = MyArr.add(arr, strNum);
                            strNum = "";
                        }
                        arr = MyArr.add(arr, ch);
                        continue;
                    }

                    // минус стоит первым символом в формуле- отрицательное число
                    if (i == 0 && (Formula.isNum(str.charAt(i + 1)))) {
//                        System.out.println("@2");
                        strNum += ch;
                        continue;
                    }

                    // минус стоит не первым символом в формуле
                    if (i > 0 ) {

                        //предыдущий символ тоже был мат.оператором, значит этот минус тоже запишем мат. оператором -  потом определим как ошибку два оператора подряд: "--"
                        if(Formula.isMathOperator(str.charAt(i - 1)) && str.charAt(i - 1) !='^') {
//                        System.out.println("@3");
                            arr = MyArr.add(arr, ch);
                            continue;
                        }

                        //это отрицательное число, а не минус между числами
                        if (!Formula.isNum(str.charAt(i - 1)) && !Formula.isBracketClose(str.charAt(i - 1))) {
//                        System.out.println("@4");
                            strNum += ch;
                            continue;
                        }

                    }
                    //
                }
            }

            // + - * /
            if (Formula.isMathOperator(ch)) {
                if (strNum.length() > 0) {
                    arr = MyArr.add(arr, strNum);
                    strNum = "";
                }
                arr = MyArr.add(arr, ch);
                continue;
            }

            //число
            if (Formula.isNum(ch)) {
                strNum += ch;
                continue;
            }

            //ничего из вышеперечисленного- значит абракадабра, плюсуем "в число", потом определим как ошибка
            strNum += ch;
        }

        // в конце смотрим- если собрано число, записываем
        if (strNum.length() > 0) {
            arr = MyArr.add(arr, strNum);
        }

        return arr;
    }


    //проверка формулы на корректность
    public static boolean checkFormulaCorrect(String[] arr) {

        //ошибки количества элементов в формуле
        if(arr.length <= 0) {
            System.out.println(Formula.MSG_ERR_SIZE);
            return false;
        }

        //парные случаи ошибок
        for (int i = 0; i < arr.length - 1; i++) {
            String first = arr[i];
            String second = arr[i + 1];

            // "1("
            if(Formula.isDouble(first)  && Formula.isBracketOpen(second)) {
                System.out.printf(STR_FORMAT_INVALID_REC, first, second);
                return false;
            }

            // ")1"
            if(Formula.isBracketClose(first)  && Formula.isDouble(second)) {
                System.out.printf(STR_FORMAT_INVALID_REC, first, second);
                return false;
            }

            // ")("
            if(Formula.isBracketClose(first)  && Formula.isBracketOpen(second)) {
                System.out.printf(STR_FORMAT_INVALID_REC, first, second);
                return false;
            }

            // "()"
            if(Formula.isBracketOpen(first) &&  Formula.isBracketClose(second)) {
                System.out.printf(STR_FORMAT_INVALID_REC, first, second);
                return false;
            }

            // "(+"
            if(Formula.isBracketOpen(first) && Formula.isMathOperator(second)) {
                System.out.printf(STR_FORMAT_INVALID_REC, first, second);
                return false;
            }

            // "+)"
            if(Formula.isMathOperator(first) && Formula.isBracketClose(second)) {
                System.out.printf(STR_FORMAT_INVALID_REC, first, second);
                return false;
            }

            // "++"
            if(Formula.isMathOperator(first) && Formula.isMathOperator(second)) {
                System.out.printf(STR_FORMAT_INVALID_REC, first, second);
                return false;
            }

            // " 5 5" - два числа подряд. На всякий случай, такое ввести не получится - два числа через пробел "склеятся" в одно
            if(Formula.isDouble(first) && Formula.isDouble(second)) {
                System.out.printf(STR_FORMAT_TWO_NUM, first, second);
                return false;
            }
        }

        //персональные случаи ошибок
        int cntBracket = 0;
        for (String str : arr) {

            //муть в формуле
            if(!Formula.isMathOperator(str) && !Formula.isDouble(str) && !Formula.isBracket(str)) {
                System.out.printf("%s %s \"%s\"  \n", MSG_ERR, MSG_INVALID_REC, str);
                return false;
            }

            //Счетчик открытия скобок
            if(Formula.isBracketOpen(str)) {
                cntBracket++;
            }
            else if(Formula.isBracketClose(str)) {
                cntBracket--;
            }

        }

        //не сошлось количество открытых и закрытых скобок: "(( 1 + 2 )"
        if(cntBracket != 0) {
            String brack= (cntBracket > 0) ? BRACKET_CLOSE : BRACKET_OPEN;
            System.out.printf(STR_FORMAT_MISS_BRACK, brack);
            return false;
        }

        //ошибка - первый оператор: "* 1 + 2 + 3"
        if(Formula.isMathOperator(arr[0])) {
            System.out.printf("%s в начале формулы перед операторм \"%s\" нет числа \n", MSG_ERR, arr[0] );
            return false;
        }

        //ошибка - последний оператор: "1 + 2 * 3 +"
        if(Formula.isMathOperator(arr[arr.length - 1])) {
            System.out.printf("%s в конце формулы после оператора \"%s\" нет числа \n", MSG_ERR, arr[arr.length - 1] );
            return false;
        }

        //слишком мало элементов в формуле
        if(arr.length <= 1) {
            System.out.println(Formula.MSG_TOO_FEW);
            return false;
        }

        return true;
    }


    //это скобки?
    public static boolean isBracket(String str){
        return (Formula.cmpStr(str, Formula.BRACKET_OPEN) || Formula.cmpStr(str, Formula.BRACKET_CLOSE));
    }

    public static boolean isBracket(char ch) {
        String str = Character.toString(ch);
        return isBracket(str);
    }

    public static boolean isBracketOpen(String str){
        return Formula.cmpStr(str,Formula.BRACKET_OPEN);
    }

    public static boolean isBracketClose(String str){
        return Formula.cmpStr(str,Formula.BRACKET_CLOSE);
    }

    public static boolean isBracketClose(char ch){
        return isBracketClose(Character.toString(ch));
    }


    //это математический оператор?
    public static boolean isMathOperator(String str){
        for (String[] arr : Formula.operators) {
            if(isMathOperator(arr, str)) {
                return true;
            }
        }

        return false;
    }

    public static boolean isMathOperator(String[] arr, String str) {
        for (String tmp : arr ) {
            if(Formula.cmpStr(tmp, str)) {
                return true;
            }
        }
        return false;
    }

    public static boolean isMathOperator(char ch) {
        String str = Character.toString(ch);
        return isMathOperator(str);
    }

    //символ может быть частью строки double ?
    public static boolean isNum(char ch) {
        if(ch == '.') {         //точка- разделитель в double
            return true;
        }

        return Formula.isDouble(Character.toString(ch));
    }

    //это double?
    public static boolean isDouble(String str) {
        try {
            Double.parseDouble(str);
        } catch (NumberFormatException e) {
            return false;
        }
        return true;
    }

    //сравнение строк
    public static boolean cmpStr(String str1, String str2) {
        return (str1.compareToIgnoreCase(str2) == 0);
    }

}
