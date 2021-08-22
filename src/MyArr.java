//операции с массивами: добавить, удалить, вставить и т.д.
public class MyArr  {

    private MyArr() {
    }

    //
    public static String[] add(String[] arr, String str) {
        String[] tmp = new String[arr.length + 1];
        for (int i = 0; i < arr.length; i++) {
            tmp[i] = arr[i];
        }
        tmp[tmp.length - 1] = str;

        return tmp;
    }


    public static String[] add(String[] arr, char ch) {
        String str = Character.toString(ch);
        return add(arr, str);
    }

    //
    public static String[] del(String[] arr, int pos, int num)
    {

        String[] tmp = new String[arr.length - num];

        for (int i = 0; i < tmp.length; i++) {
            if(i < pos) {
                tmp[i] = arr[i];
            }
            else {
                tmp[i] = arr[i + num];
            }
        }
        return tmp;
    }

    public static String[] del(String[] arr, int pos)
    {
        return del(arr, pos, 1);
    }

    //
    public static String[] insert(String[] arr, String str, int pos)
    {
        String[] tmp = new String[arr.length + 1];

        for (int i = 0; i < tmp.length; i++) {
            if(i < pos) {
                tmp[i] = arr[i];
            }
            else if(i == pos){
                tmp[i] = str;
            }
            else {
                tmp[i] = arr[i - 1];
            }
        }
        return tmp;
    }

    //массив в строку
    public static String arrToStr(String[] arr, String split) {
        String str = "";

        for (int i = 0; i < arr.length; i++) {
            str += arr[i];
            if(i < arr.length - 1 ) {
                str += split;
            }
        }

        return str;
    }

    public static String arrToStr(String[] arr) {
        return arrToStr(arr, " ");
    }

    public static String arrToStr(String[][] arr, String split) {
        String str = "";

        for (int i = 0; i < arr.length; i++) {
            str += arrToStr(arr[i], split);
            if(i < arr.length - 1 ) {
                str += split;
            }
        }
        return str;
    }


    //распечатать массив
    public static void printlnArr(String[] arr, String split) {
        System.out.println(arrToStr(arr, split));
    }

    public static void printlnArr(String[] arr) {
        printlnArr(arr, " ");
    }

    public static void printlnArr(String[][] arr, String split) {
        System.out.println(arrToStr(arr, split));
    }

    public static void printlnArr(String[][] arr) {
        printlnArr(arr, " ");
    }

}
