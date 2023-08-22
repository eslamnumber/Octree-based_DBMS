import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class eslam extends Sherif {
    public static int x ;
    private  eslam(int n ){
        this.nn++;
        if(x==0)
            x++;
        else
            throw new RuntimeException("You can't create more than one object");
    }
    public int getMax(List<Integer> arr){
        int max = 0;
        for(int i =0;i<arr.size();i++){
            if(arr.get(i)>max) {
                max = arr.get(i);
            }
        }
        return max;
    }
    public int getMin(List<Integer> arr) {
        int min = Integer.MAX_VALUE;
        for (int i = 0; i < arr.size(); i++) {
            if (arr.get(i)< min) {
                min = arr.get(i);
            }
        }
        return min;
    }

    public int NoOfOperations(List<Integer>  arr){
        int max = getMax(arr);
        int min =getMin(arr);
        int count = 0;
        while(max!=min) {
            int n =max - min;
            for (int i = 0; i <arr.size(); i++) {
                if(arr.get(i)==max)
                    continue;
                if(n>=5)
                    arr.add(i,arr.get(i)+5);
                else if (n>=2)
                    arr.add(i,arr.get(i)+2);
                else
                    arr.add(i,arr.get(i)+1);

            }
            count ++;
            max = getMax(arr);
            min = getMin(arr);
        }
        return count;
    }
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        int t = sc.nextInt();
        while (t-->0){
            int [] arr = new int[sc.nextInt()+1];
            for(int i =0;i<arr.length;i++){
                int nn= sc.nextInt();
                if(nn>=arr.length) {
                    System.out.println("NO");
                    continue;
                }
                arr[nn]++;
            }
            boolean flag = true;
            for(int i =0;i<arr.length;i++){
                if(arr[i]==0) {
                    System.out.println("NO");
                    flag = false;
                    break;
                }
                if(flag)
                    System.out.println("YES");
            }
        }
    }
//    public static void main (String[] args){
//        Scanner sc = new Scanner(System.in);
//        int t = sc.nextInt();
//        while (t-->0) {
//            int n = sc.nextInt();
//            int j =sc.nextInt();
//            int k =sc.nextInt();
//            int x = (k*j)%n;
//            int nOofCoins = (k*j)/n;
//            int SavedCoins=x*n;
//            while(true){
//              int r  =(x%j);
//              if(r>=j/2) {
//                  SavedCoins += n;
//                  nOofCoins--;
//              }
//              else
//                    break;
//
//            }
//        System.out.println(SavedCoins);
//          }
//        }

    }

