package test;

import java.util.ArrayDeque;

public class javaCollectionPrac {
  public static void main(String[] args){
	  //用ArrayDeque来实现栈，ArrayDeque是Deque接口的实现类，是一个基于数组的双端队列
	  ArrayDeque<String> stack = new ArrayDeque<>();
	  stack.push("Monday");
	  stack.push("Tuseday");
	  stack.push("Wednesday");
	  stack.push("Thursday");
	  stack.push("Friday");
	  System.out.println(stack);
	  stack.pop();
	  System.out.println(stack);
	  
	  //用ArrayDeque来实现队列
	  ArrayDeque<String> queue = new ArrayDeque<>();
	  queue.offer("Saturday");
	  queue.offer("Sunday");
	  System.out.println(queue);
	  queue.peek();
	  System.out.println("queue peek method" + queue);
	  queue.poll();
	  System.out.println(queue);
  }
}
