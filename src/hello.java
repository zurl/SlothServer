public class hello {
    public static void main(String[] x){
        int[] ret = new int[100005];ret[0] = 1;int flag = 0;
        for(int i=1;i<=10;i++){
            for(int j=0;j<99999;j++)ret[j] *=i;
            for(int j=0;j<99999;j++){if(ret[j]>=10)ret[j+1] += ret[j]/10;ret[j]%=10;}
        }
        for(int j=99999;j>=0;j--){
            if(ret[j] !=0 ) flag = 1;
            if(flag == 1) System.out.print(ret[j]);
        }
    }
}
