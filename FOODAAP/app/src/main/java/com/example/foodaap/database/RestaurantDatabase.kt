package com.example.foodaap.database



    import androidx.room.Database
    import androidx.room.RoomDatabase


@Database(entities = [RestaurantEntity::class, OrderEntity::class], version = 2)
abstract class RestaurantDatabase : RoomDatabase() {

    abstract fun restaurantDao(): RestaurantDao
    abstract fun orderDao() : OrderDao
    }

