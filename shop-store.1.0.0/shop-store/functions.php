<?php

// Load CSS files.
function shop_store_enqueue_style() {
	// Parent theme CSS.
    wp_enqueue_style( 'di-blog-style-default', get_template_directory_uri() . '/style.css' );

    // Shop Store css files.
    wp_enqueue_style( 'shop-store-style',  get_stylesheet_directory_uri() . '/style.css', array( 'bootstrap', 'font-awesome', 'di-blog-style-default', 'di-blog-style-core' ), wp_get_theme()->get('Version'), 'all' );
}
add_action( 'wp_enqueue_scripts', 'shop_store_enqueue_style' );

// Recommended plugins.
function shop_store_plugins() {

	$plugins = array(
		array(
			'name'      => __( 'WooCommerce PDF Invoices & Packing Slips', 'shop-store'),
			'slug'      => 'woocommerce-pdf-invoices-packing-slips',
			'required'  => false,
		),
		array(
			'name'      => __( 'Woo Quick View', 'shop-store'),
			'slug'      => 'woo-quick-view',
			'required'  => false,
		),
	);

	tgmpa( $plugins );
}
add_action( 'tgmpa_register', 'shop_store_plugins' );

