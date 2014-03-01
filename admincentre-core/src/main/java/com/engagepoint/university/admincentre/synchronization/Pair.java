package com.engagepoint.university.admincentre.synchronization;

/**
 * Class could be used to store a pair of objects.
 *
 * @param <L> to store left object
 * @param <R> to store right object
 * 
 * @author roman.garkavenko
 */
public class Pair<L,R> {
    private L left;
    private R right;
    public Pair(L left, R right){
        this.left = left;
        this.right = right;
    }
    
    public L getLeft(){
    	return left;
    }
    
    public R getRight(){
    	return right;
    }
    
    @Override
    public String toString() {
    	return left.toString() + "\t" + right.toString();
    }
}