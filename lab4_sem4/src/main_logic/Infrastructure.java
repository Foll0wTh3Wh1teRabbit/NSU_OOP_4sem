package main_logic;

import consume.Consumer;
import delivery.service_interface.DeliveryService;
import delivery.train_delivery.TrainDeliveryService;
import factory.Factory;
import product.ProductType;
import warehouse.ArrivalWarehouse;
import warehouse.DepartureWarehouse;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class Infrastructure {
    private static final Properties properties = new Properties();;

    private static final ArrayList <Factory> factories;
    private static final DeliveryService deliveryService;
    private static final ArrayList <Consumer> consumers;
    private static final Map <ProductType, DepartureWarehouse> departures;
    private static final Map <ProductType, ArrivalWarehouse> arrivals;

    static {
        try {
            InputStream inputStream = Infrastructure.class.getResourceAsStream("/manufacture.config");
            properties.load(inputStream);
        } catch (IOException e) {
            throw new ExceptionInInitializerError(e);
        }

        departures = new HashMap<>();
        arrivals = new HashMap<>();

        deliveryService = new TrainDeliveryService();

        String[] productTypes = getProperties().get("factory_productTypes").toString().split(", ");
        int requiredNumberOfElements = Integer.parseInt(getProperties().get("factory_number").toString());
        if (requiredNumberOfElements != productTypes.length) {
            throw new ExceptionInInitializerError("Wrong config data!");
        }

        factories = new ArrayList<>();
        for (int i = 0; i < requiredNumberOfElements; ++i) {
            String currentTypeLiteral = productTypes[i];
            factories.add(new Factory(new ProductType(currentTypeLiteral)));
        }

        consumers = new ArrayList<>();
        for (int i = 0; i < requiredNumberOfElements; ++i) {
            consumers.add(new Consumer());
        }
    }

    public static void start() {
        //ExecutorService executorService = Executors.newCachedThreadPool();

        for (int i = 0; i < getConsumers().size(); ++i) {
            new Thread(getConsumers().get(i)).start();
        }

        for (int i = 0; i < getDeliveryService().getDepot().getTransport().size(); ++i) {
            new Thread(getDeliveryService().getDepot().getTransport().get(i)).start();
        }

        for (int i = 0; i < getFactories().size(); ++i) {
            new Thread(getFactories().get(i)).start();
        }

        //executorService.shutdown();
    }

    public static Properties getProperties() {
        return properties;
    }

    public static ArrayList <Factory> getFactories() {
        return factories;
    }

    public static DeliveryService getDeliveryService() {
        return deliveryService;
    }

    public static ArrayList <Consumer> getConsumers() {
        return consumers;
    }

    public static Map<ProductType, DepartureWarehouse> getDepartures() {
        return departures;
    }

    public static Map<ProductType, ArrivalWarehouse> getArrivals() {
        return arrivals;
    }
}
