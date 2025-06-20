-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Generation Time: Jun 20, 2025 at 09:36 AM
-- Server version: 10.4.32-MariaDB
-- PHP Version: 8.2.12

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `project`
--

-- --------------------------------------------------------

--
-- Table structure for table `carts`
--

CREATE TABLE `carts` (
  `cart_id` bigint(20) NOT NULL,
  `user_id` bigint(20) NOT NULL,
  `created_at` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `carts`
--

INSERT INTO `carts` (`cart_id`, `user_id`, `created_at`) VALUES
(3, 21, '2025-06-17 04:10:49');

-- --------------------------------------------------------

--
-- Table structure for table `cart_items`
--

CREATE TABLE `cart_items` (
  `cart_item_id` bigint(20) NOT NULL,
  `cart_id` bigint(20) NOT NULL,
  `product_id` bigint(20) NOT NULL,
  `quantity` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `categories`
--

CREATE TABLE `categories` (
  `category_id` bigint(20) NOT NULL,
  `name` varchar(100) NOT NULL,
  `description` text DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `categories`
--

INSERT INTO `categories` (`category_id`, `name`, `description`) VALUES
(1, 'Dairy, Bread & Eggs', 'All dairy product, Breads and eggs'),
(2, 'Fruits & Vegetables', 'Green and leafy vegetables and fruits'),
(3, 'Chicken, Meat & Fish', 'All non-vegetarian items'),
(4, 'Cold Drinks & Juices', 'All type of cold drinks and juices'),
(5, 'Snacks & Munchies', 'Fresh snacks'),
(6, 'Breakfast & Instant Food', 'Healthy breackfast'),
(7, 'Bakery & Biscuits', 'All type of Bakery and Biscuits');

-- --------------------------------------------------------

--
-- Table structure for table `discounts`
--

CREATE TABLE `discounts` (
  `discount_id` bigint(20) NOT NULL,
  `seller_id` bigint(20) NOT NULL,
  `category_id` bigint(20) DEFAULT NULL,
  `product_id` bigint(20) DEFAULT NULL,
  `discount_percent` decimal(5,2) NOT NULL,
  `start_date` timestamp NULL DEFAULT NULL,
  `end_date` timestamp NULL DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `orders`
--

CREATE TABLE `orders` (
  `order_id` bigint(20) NOT NULL,
  `user_id` bigint(20) NOT NULL,
  `total_price` decimal(10,2) NOT NULL,
  `status` enum('PENDING','SHIPPED','DELIVERED','CANCELLED') NOT NULL DEFAULT 'PENDING',
  `created_at` timestamp NOT NULL DEFAULT current_timestamp(),
  `updated_at` timestamp NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `orders`
--

INSERT INTO `orders` (`order_id`, `user_id`, `total_price`, `status`, `created_at`, `updated_at`) VALUES
(7, 21, 181.65, 'SHIPPED', '2025-06-18 05:02:57', '2025-06-19 04:39:03'),
(8, 21, 58.00, 'PENDING', '2025-06-19 06:25:30', '2025-06-19 06:25:30');

-- --------------------------------------------------------

--
-- Table structure for table `order_items`
--

CREATE TABLE `order_items` (
  `order_item_id` bigint(20) NOT NULL,
  `order_id` bigint(20) NOT NULL,
  `product_id` bigint(20) NOT NULL,
  `quantity` int(11) NOT NULL,
  `price_at_purchase` decimal(10,2) NOT NULL,
  `discount_applied` decimal(5,2) DEFAULT 0.00
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `order_items`
--

INSERT INTO `order_items` (`order_item_id`, `order_id`, `product_id`, `quantity`, `price_at_purchase`, `discount_applied`) VALUES
(7, 7, 8, 3, 60.55, 0.00),
(8, 8, 11, 1, 35.00, 0.00),
(9, 8, 13, 1, 5.00, 0.00),
(10, 8, 10, 1, 18.00, 0.00);

-- --------------------------------------------------------

--
-- Table structure for table `products`
--

CREATE TABLE `products` (
  `product_id` bigint(20) NOT NULL,
  `seller_id` bigint(20) NOT NULL,
  `name` varchar(150) NOT NULL,
  `description` text DEFAULT NULL,
  `price` decimal(10,2) NOT NULL,
  `stock_quantity` int(11) NOT NULL,
  `category_id` bigint(20) DEFAULT NULL,
  `created_at` timestamp NOT NULL DEFAULT current_timestamp(),
  `updated_at` timestamp NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `products`
--

INSERT INTO `products` (`product_id`, `seller_id`, `name`, `description`, `price`, `stock_quantity`, `category_id`, `created_at`, `updated_at`) VALUES
(8, 17, 'Amul Butter', 'Amul butter made from pure cow milk', 60.55, 20, 1, '2025-06-14 06:55:36', '2025-06-19 03:11:05'),
(10, 17, 'Haldiram\'s Sev Bhujia', 'Haldiram\'s Sev Bhujia limited edition ', 18.00, 19, 5, '2025-06-15 07:31:19', '2025-06-19 06:25:30'),
(11, 17, 'Britannia Cheese Slices', 'Fresh Cheese', 35.00, 9, 1, '2025-06-15 07:32:30', '2025-06-19 06:25:30'),
(12, 17, 'Salted Instant Popcorn', 'Popcorns in 5 mins', 22.50, 40, 6, '2025-06-15 07:34:00', '2025-06-16 07:41:39'),
(13, 17, 'Slurrp Millet Chocolate', 'Fresh Chocolate', 5.00, 49, 5, '2025-06-15 08:07:30', '2025-06-19 06:25:30');

-- --------------------------------------------------------

--
-- Table structure for table `product_images`
--

CREATE TABLE `product_images` (
  `image_id` bigint(20) NOT NULL,
  `product_id` bigint(20) NOT NULL,
  `image_path` varchar(255) NOT NULL,
  `created_at` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `product_images`
--

INSERT INTO `product_images` (`image_id`, `product_id`, `image_path`, `created_at`) VALUES
(6, 8, '/uploads/images/d05bc5f0-eaa0-4fd1-aaf7-4b2d2fe1bbb9_product-img-10.jpg', '2025-06-14 06:55:36'),
(8, 10, '/uploads/images/5a13ba5a-93d1-4cc7-9368-25c56fae0bbb_product-img-1.jpg', '2025-06-15 07:31:19'),
(9, 11, '/uploads/images/b76eadc0-de3a-4bdb-b2a3-73f4e4c34cc1_product-img-7.jpg', '2025-06-15 07:32:30'),
(10, 12, '/uploads/images/e935a72a-9bf6-47f2-9083-31dd1a6fb889_product-img-5.jpg', '2025-06-15 07:34:00'),
(11, 13, '/uploads/images/a350c1f2-9894-4831-912e-97382236b894_product-img-9.jpg', '2025-06-15 08:07:30');

-- --------------------------------------------------------

--
-- Table structure for table `reviews`
--

CREATE TABLE `reviews` (
  `review_id` bigint(20) NOT NULL,
  `user_id` bigint(20) NOT NULL,
  `product_id` bigint(20) NOT NULL,
  `rating` int(11) NOT NULL CHECK (`rating` >= 1 and `rating` <= 5),
  `comment` text DEFAULT NULL,
  `created_at` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `seller_analytics`
--

CREATE TABLE `seller_analytics` (
  `analytic_id` bigint(20) NOT NULL,
  `seller_id` bigint(20) NOT NULL,
  `total_sales` decimal(15,2) DEFAULT 0.00,
  `total_orders` int(11) DEFAULT 0,
  `top_product_id` bigint(20) DEFAULT NULL,
  `last_updated` timestamp NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `token_blacklist`
--

CREATE TABLE `token_blacklist` (
  `id` bigint(20) NOT NULL,
  `token` varchar(500) NOT NULL,
  `blacklisted_at` timestamp NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `token_blacklist`
--

INSERT INTO `token_blacklist` (`id`, `token`, `blacklisted_at`) VALUES
(1, 'eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIxOCIsInJvbGUiOiJVU0VSIiwiaWF0IjoxNzQ4MDcyNzE4LCJleHAiOjE3NDgxNTkxMTh9.dFWxwnPvm6qlXh2o4FbTaGIzQOzS0-oYlejLflx5gAg', '2025-05-24 07:46:58'),
(2, 'eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIxNyIsInJvbGUiOiJTRUxMRVIiLCJpYXQiOjE3NDgyNDI0ODgsImV4cCI6MTc0ODMyODg4OH0.PTzCwwZwodBDAbRnuj8n_2UyJoWjJl5A7-mdIhbeQtc', '2025-05-26 06:57:12'),
(3, 'eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIxOSIsInJvbGUiOiJVU0VSIiwiaWF0IjoxNzQ4MzMxNTU2LCJleHAiOjE3NDg0MTc5NTZ9.ICkyyc_6iRaSjB30n4i5O-FAKs5zUrwDXKpOFR3b0xw', '2025-05-27 07:40:39'),
(4, 'eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIyMCIsInJvbGUiOiJVU0VSIiwiaWF0IjoxNzQ4NDM1MjE1LCJleHAiOjE3NDg1MjE2MTV9.Jd9evbZKF_SRsW6Sb4zX1dli3IDlErBS4m06rXBoq8Q', '2025-05-28 12:28:35'),
(5, 'eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIyMCIsInJvbGUiOiJVU0VSIiwiaWF0IjoxNzQ4NzU2NjYwLCJleHAiOjE3NDg4NDMwNjB9.S0LO0rrdAmwTs-cC8z5REFU0KwfY6wM2RGg6yWDCWJA', '2025-06-01 05:46:02'),
(6, 'eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIxNyIsInJvbGUiOiJTRUxMRVIiLCJpYXQiOjE3NDk3OTIzNzgsImV4cCI6MTc0OTg3ODc3OH0.6UEjz8ZcP0TLKjMCsmge1dTD5NAte3PZaFi0HKqaJNU', '2025-06-13 05:26:30'),
(7, 'eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIxNyIsInJvbGUiOiJTRUxMRVIiLCJpYXQiOjE3NDk3OTMxNDEsImV4cCI6MTc0OTg3OTU0MX0.fVHjZFhjstaeRxIbu8XrAjYXJ9pJ4hz3bpNW8nIQ4b0', '2025-06-13 05:39:32'),
(8, 'eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIxNyIsInJvbGUiOiJTRUxMRVIiLCJpYXQiOjE3NDk3OTUwMjQsImV4cCI6MTc0OTg4MTQyNH0.zF6N2eVHyqmu32eTWKutAkNGFnBGL85bNjmYNw-NmZo', '2025-06-13 06:10:57'),
(9, 'eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIxNyIsInJvbGUiOiJTRUxMRVIiLCJpYXQiOjE3NDk3OTgwOTQsImV4cCI6MTc0OTg4NDQ5NH0.eDASDH7lAfkS70igF36LHZB1MY_5YaL9fE2alr5uoaM', '2025-06-13 07:03:20'),
(10, 'eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIxNyIsInJvbGUiOiJTRUxMRVIiLCJpYXQiOjE3NDk5NzI0MTAsImV4cCI6MTc1MDA1ODgxMH0.r0x7DVYLOQS2MEKLsb7KDwR0aaAakAgq7c_0IsSTiDQ', '2025-06-15 07:34:08'),
(11, 'eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIyMSIsInJvbGUiOiJVU0VSIiwiaWF0IjoxNzUwMTQwNjc5LCJleHAiOjE3NTAyMjcwNzl9.nS2fZHhdf86iAlc3oypk6SfVFQ3EUJd06s077zUWlY0', '2025-06-17 06:21:24'),
(12, 'eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIyMSIsInJvbGUiOiJVU0VSIiwiaWF0IjoxNzUwMzE0MjE4LCJleHAiOjE3NTA0MDA2MTh9.U6jbmaJMzjR9T9fpTIuvZWpDLHY8krq2sVVnUvUyMO4', '2025-06-19 06:25:44'),
(13, 'eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIxNyIsInJvbGUiOiJTRUxMRVIiLCJpYXQiOjE3NTAzMTQzNTIsImV4cCI6MTc1MDQwMDc1Mn0.Na2wjeE1PGO7eAt8QQuflLy7JTgfFCLQdYvBI5u7w0M', '2025-06-19 06:28:24'),
(14, 'eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIyMSIsInJvbGUiOiJVU0VSIiwiaWF0IjoxNzUwMzE2MzY4LCJleHAiOjE3NTA0MDI3Njh9.E5DVyv_zDV_GZjPzRucKcr1qBWZcVH1F5x3bSKB2N3k', '2025-06-19 07:00:24');

-- --------------------------------------------------------

--
-- Table structure for table `users`
--

CREATE TABLE `users` (
  `user_id` bigint(20) NOT NULL,
  `name` varchar(100) NOT NULL,
  `email` varchar(100) NOT NULL,
  `password` varchar(255) NOT NULL,
  `phone` varchar(20) DEFAULT NULL,
  `address` text DEFAULT NULL,
  `role` enum('USER','SELLER') NOT NULL,
  `profile_picture` varchar(255) DEFAULT NULL,
  `created_at` timestamp NOT NULL DEFAULT current_timestamp(),
  `updated_at` timestamp NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `users`
--

INSERT INTO `users` (`user_id`, `name`, `email`, `password`, `phone`, `address`, `role`, `profile_picture`, `created_at`, `updated_at`) VALUES
(1, 'Jane Doe', 'alice@example.com', '$2a$12$1duSdizI7/l/5XuatWL74uFyfeRMdj7tRn9CkBXEI4Zk/4UPz9Ove', '1112223333', '789 Market St', 'USER', 'profile.jpg', '2025-05-19 03:11:16', '2025-06-15 05:34:46'),
(2, 'Jane Doe', 'bob@example.com', '$2a$12$R/kef7/2Qkj.QenEP.itneZgRvYbWXTefBRf5loV9HiXMBT313Iri', '1112223333', '789 Market St', 'SELLER', 'profile.jpg', '2025-05-19 03:11:16', '2025-06-15 05:35:11'),
(17, 'Seller', 'testseller@example.com', '$2a$10$rwgMz81NRTvF7CHKmArf9OwrUqLrLM.Zv/zAWTlI4XGBF5MZS/Bly', '9874563310', '123 Update Street', 'SELLER', NULL, '2025-05-24 06:56:02', '2025-06-14 04:24:31'),
(19, 'Updated User', 'testuser@example.com', '$2a$10$n7J65iX7QSuQwuSn8MJCI.ZwSlaALOtomEPGhjW5qtOx69gHBotKW', '0987654321', '123 Updated Lane', 'USER', 'http://example.com/profile.jpg', '2025-05-27 03:47:40', '2025-05-27 03:49:09'),
(21, 'Ujjval', 'ujjvalpatel210@gmail.com', '$2a$10$PB98BrogMFmol.ayDp0UPenGSzL1zJrsKE3f5ACD.DxWog5efr9ri', '09106320495', 'fgfufhefhweiojrehiejfiowejfwuhfioejfi', 'USER', NULL, '2025-06-15 05:33:25', '2025-06-15 05:33:25');

--
-- Indexes for dumped tables
--

--
-- Indexes for table `carts`
--
ALTER TABLE `carts`
  ADD PRIMARY KEY (`cart_id`),
  ADD KEY `user_id` (`user_id`);

--
-- Indexes for table `cart_items`
--
ALTER TABLE `cart_items`
  ADD PRIMARY KEY (`cart_item_id`),
  ADD UNIQUE KEY `unique_cart_product` (`cart_id`,`product_id`),
  ADD KEY `product_id` (`product_id`);

--
-- Indexes for table `categories`
--
ALTER TABLE `categories`
  ADD PRIMARY KEY (`category_id`);

--
-- Indexes for table `discounts`
--
ALTER TABLE `discounts`
  ADD PRIMARY KEY (`discount_id`),
  ADD KEY `seller_id` (`seller_id`),
  ADD KEY `category_id` (`category_id`),
  ADD KEY `product_id` (`product_id`);

--
-- Indexes for table `orders`
--
ALTER TABLE `orders`
  ADD PRIMARY KEY (`order_id`),
  ADD KEY `user_id` (`user_id`),
  ADD KEY `idx_order_status` (`status`);

--
-- Indexes for table `order_items`
--
ALTER TABLE `order_items`
  ADD PRIMARY KEY (`order_item_id`),
  ADD KEY `order_id` (`order_id`),
  ADD KEY `product_id` (`product_id`);

--
-- Indexes for table `products`
--
ALTER TABLE `products`
  ADD PRIMARY KEY (`product_id`),
  ADD KEY `seller_id` (`seller_id`),
  ADD KEY `category_id` (`category_id`),
  ADD KEY `idx_product_name` (`name`);

--
-- Indexes for table `product_images`
--
ALTER TABLE `product_images`
  ADD PRIMARY KEY (`image_id`),
  ADD KEY `product_id` (`product_id`);

--
-- Indexes for table `reviews`
--
ALTER TABLE `reviews`
  ADD PRIMARY KEY (`review_id`),
  ADD KEY `user_id` (`user_id`),
  ADD KEY `product_id` (`product_id`);

--
-- Indexes for table `seller_analytics`
--
ALTER TABLE `seller_analytics`
  ADD PRIMARY KEY (`analytic_id`),
  ADD KEY `seller_id` (`seller_id`),
  ADD KEY `top_product_id` (`top_product_id`);

--
-- Indexes for table `token_blacklist`
--
ALTER TABLE `token_blacklist`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `token` (`token`);

--
-- Indexes for table `users`
--
ALTER TABLE `users`
  ADD PRIMARY KEY (`user_id`),
  ADD UNIQUE KEY `email` (`email`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `carts`
--
ALTER TABLE `carts`
  MODIFY `cart_id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=4;

--
-- AUTO_INCREMENT for table `cart_items`
--
ALTER TABLE `cart_items`
  MODIFY `cart_item_id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=12;

--
-- AUTO_INCREMENT for table `categories`
--
ALTER TABLE `categories`
  MODIFY `category_id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=10;

--
-- AUTO_INCREMENT for table `discounts`
--
ALTER TABLE `discounts`
  MODIFY `discount_id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=6;

--
-- AUTO_INCREMENT for table `orders`
--
ALTER TABLE `orders`
  MODIFY `order_id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=9;

--
-- AUTO_INCREMENT for table `order_items`
--
ALTER TABLE `order_items`
  MODIFY `order_item_id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=11;

--
-- AUTO_INCREMENT for table `products`
--
ALTER TABLE `products`
  MODIFY `product_id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=15;

--
-- AUTO_INCREMENT for table `product_images`
--
ALTER TABLE `product_images`
  MODIFY `image_id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=13;

--
-- AUTO_INCREMENT for table `reviews`
--
ALTER TABLE `reviews`
  MODIFY `review_id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=4;

--
-- AUTO_INCREMENT for table `seller_analytics`
--
ALTER TABLE `seller_analytics`
  MODIFY `analytic_id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=2;

--
-- AUTO_INCREMENT for table `token_blacklist`
--
ALTER TABLE `token_blacklist`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=15;

--
-- AUTO_INCREMENT for table `users`
--
ALTER TABLE `users`
  MODIFY `user_id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=22;

--
-- Constraints for dumped tables
--

--
-- Constraints for table `carts`
--
ALTER TABLE `carts`
  ADD CONSTRAINT `carts_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`) ON DELETE CASCADE;

--
-- Constraints for table `cart_items`
--
ALTER TABLE `cart_items`
  ADD CONSTRAINT `cart_items_ibfk_1` FOREIGN KEY (`cart_id`) REFERENCES `carts` (`cart_id`) ON DELETE CASCADE,
  ADD CONSTRAINT `cart_items_ibfk_2` FOREIGN KEY (`product_id`) REFERENCES `products` (`product_id`);

--
-- Constraints for table `discounts`
--
ALTER TABLE `discounts`
  ADD CONSTRAINT `discounts_ibfk_1` FOREIGN KEY (`seller_id`) REFERENCES `users` (`user_id`) ON DELETE CASCADE,
  ADD CONSTRAINT `discounts_ibfk_2` FOREIGN KEY (`category_id`) REFERENCES `categories` (`category_id`) ON DELETE SET NULL,
  ADD CONSTRAINT `discounts_ibfk_3` FOREIGN KEY (`product_id`) REFERENCES `products` (`product_id`) ON DELETE CASCADE;

--
-- Constraints for table `orders`
--
ALTER TABLE `orders`
  ADD CONSTRAINT `orders_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`) ON DELETE CASCADE;

--
-- Constraints for table `order_items`
--
ALTER TABLE `order_items`
  ADD CONSTRAINT `order_items_ibfk_1` FOREIGN KEY (`order_id`) REFERENCES `orders` (`order_id`) ON DELETE CASCADE,
  ADD CONSTRAINT `order_items_ibfk_2` FOREIGN KEY (`product_id`) REFERENCES `products` (`product_id`);

--
-- Constraints for table `products`
--
ALTER TABLE `products`
  ADD CONSTRAINT `products_ibfk_1` FOREIGN KEY (`seller_id`) REFERENCES `users` (`user_id`) ON DELETE CASCADE,
  ADD CONSTRAINT `products_ibfk_2` FOREIGN KEY (`category_id`) REFERENCES `categories` (`category_id`) ON DELETE CASCADE;

--
-- Constraints for table `product_images`
--
ALTER TABLE `product_images`
  ADD CONSTRAINT `product_images_ibfk_1` FOREIGN KEY (`product_id`) REFERENCES `products` (`product_id`) ON DELETE CASCADE;

--
-- Constraints for table `reviews`
--
ALTER TABLE `reviews`
  ADD CONSTRAINT `reviews_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`) ON DELETE CASCADE,
  ADD CONSTRAINT `reviews_ibfk_2` FOREIGN KEY (`product_id`) REFERENCES `products` (`product_id`);

--
-- Constraints for table `seller_analytics`
--
ALTER TABLE `seller_analytics`
  ADD CONSTRAINT `seller_analytics_ibfk_1` FOREIGN KEY (`seller_id`) REFERENCES `users` (`user_id`) ON DELETE CASCADE,
  ADD CONSTRAINT `seller_analytics_ibfk_2` FOREIGN KEY (`top_product_id`) REFERENCES `products` (`product_id`) ON DELETE SET NULL;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
