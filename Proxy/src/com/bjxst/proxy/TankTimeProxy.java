package com.bjxst.proxy;


public class TankTimeProxy implements Moveable {
private Moveable t;
public TankTimeProxy(Moveable t){
super();
this.t = t;
}
@Override
public void  stop(){
h.invoke(this,public abstract void com.bjxst.proxy.Moveable.stop());
}@Override
public void  move(){
h.invoke(this,public abstract void com.bjxst.proxy.Moveable.move());
}}