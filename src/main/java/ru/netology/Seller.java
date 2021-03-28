package ru.netology;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

import static ru.netology.Main.log;

public class Seller {

    final int TIME_SELLER_PROCESSING = 2000;
    final int TIME_CAR_MAKING = 5000;

    private Shop shop;
    private ReentrantLock lock;
    private Condition condition;

    public Seller(Shop shop) {
        this.shop = shop;
        this.lock = new ReentrantLock(true);;
        this.condition = lock.newCondition();
    }

    public Car sellCar() {
        lock.lock();
        try{
            while (shop.getCars().size() == 0) {
                log("Машин нет. Покупателю '" + Thread.currentThread().getName() + "' нужно подождать");
                condition.await();
            }
            log(Thread.currentThread().getName() + " получил сигнал о наличии машины");
            Thread.sleep(TIME_SELLER_PROCESSING);
        }
        catch (InterruptedException ex){
            ex.printStackTrace();
        }
        finally {
            lock.unlock();
        }
        return shop.getCars().remove(0);
    }


    public void getCar() {
        lock.lock();
        try{
            Thread.sleep(TIME_CAR_MAKING);
            Car car = new CarToyota();
            shop.getCars().add(car);
            log("Производитель '" + Thread.currentThread().getName() + "' выпустил 1 авто: " + car.getBrand());
            condition.signalAll();
        }
        catch (InterruptedException ex){
            ex.printStackTrace();
        }
        finally {
            lock.unlock();
        }
    }
}
