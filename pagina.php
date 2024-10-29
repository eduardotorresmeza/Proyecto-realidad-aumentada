<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Garaje Automotriz - Inicio</title>
    
    <style>
        body {
            font-family: 'Verdana', sans-serif;
            margin: 0;
            padding: 0;
            background-color: #e5e5e5;
            color: #333;
        }

        #menu {
            display: flex;
            justify-content: space-around;
            align-items: center;
            background-color: #444;
            padding: 20px 0;
            box-shadow: 0 4px 10px rgba(0, 0, 0, 0.2);
            width: 100%;
            
        }

        #menu div {
            text-align: center;
            margin: 0 20px;
        }

        #menu img {
            width: 75px;
            height: 75px;
            cursor: pointer;
            transition: all 0.3s ease;
        }

        #menu img:hover {
            transform: scale(1.1);
        }

        #menu p {
            margin-top: 10px;
            color: #f0f0f0;
            font-size: 15px;
            font-weight: bold;
        }

        #menu a {
            text-decoration: none;
        }

        #destino {
            margin: 30px auto;
            padding: 40px;
            width: 85%;
            background-color: #fff;
            box-shadow: 0px 4px 12px rgba(0, 0, 0, 0.2);
            min-height: 300px;
        }

        .main-content {
            text-align: center;
            padding: 30px;
        }

        .main-content h2 {
            color: #444;
        }

        .product {
            display: inline-block;
            margin: 20px;
            padding: 25px;
            background-color: #f2f2f2;
            border-radius: 8px;
            box-shadow: 0 3px 8px rgba(0, 0, 0, 0.2);
            width: 220px;
        }

        .product img {
            width: 100%;
            height: auto;
            border-radius: 8px;
        }

        .product h4 {
            margin: 12px 0;
            font-size: 16px;
        }

        .product p {
            font-size: 14px;
        }

        footer {
            background-color: #333;
            color: #f0f0f0;
            padding: 50px 20px;
            margin-top: 40px;
        }

        footer .container {
            display: flex;
            justify-content: space-between;
            flex-wrap: wrap;
        }

        footer .column {
            flex: 1;
            margin: 15px 20px;
        }

        footer h3 {
            margin-bottom: 20px;
            font-size: 20px;
        }

        footer p {
            font-size: 14px;
            margin: 10px 0;
        }

        footer a {
            color: #f0f0f0;
            text-decoration: none;
            font-size: 14px;
            margin: 10px 0;
            display: block;
        }

        footer a:hover {
            text-decoration: underline;
        }

        .social-icons img {
            width: 35px;
            margin: 0 10px;
        }

        .footer-bottom {
            text-align: center;
            padding-top: 30px;
            border-top: 1px solid rgba(255, 255, 255, 0.3);
            font-size: 15px;
        }
    </style>
   
</head>
<body>

<div id="menu">
    <div>
        <a href="somos.php" id="enlaceajax"><img src="somos.png" alt="Nosotros"></a>
        <p>Nosotros</p>
    </div>
    <div>
        <a href="productos.php" id="enlaceajax1"><img src="productos.png" alt="Productos"></a>
        <p>Productos</p>
    </div>
    <div>
        <a href="servicios.php" id="enlaceajax2"><img src="servicios1.png" alt="Servicios"></a>
        <p>Servicios</p>
    </div>
    <div>
        <a href="contactos.php" id="enlaceajax3"><img src="contacto1.png" alt="Contacto"></a>
        <p>Contacto</p>
    </div>
    <div>
        <a href="otros.php" id="enlaceajax4"><img src="otros1.png" alt="Otros"></a>
        <p>Otros</p>
    </div>
    <div>
        <a href="consulta.php" id="enlaceajax5"><img src="consulta.png" alt="Consulta"></a>
        <p>Consulta</p>
    </div>
</div>

<div id="destino">
    <div class="main-content">
        <h2>Bienvenido a la Cochera Express</h2>
        <p>En nuestro cochera, ofrecemos una amplia gama de servicios y productos para automóviles. Aquí puedes encontrar los mejores accesorios y servicios de mantenimiento.</p>
        
        <h3>Nuestros Productos</h3>
        <div class="product">
            <img src="limpiador antiempañante.jpg" alt="Producto 1">
            <h4>limpiador antiempañante</h4>
            <p>Limpiador Antiempañante: Fórmula avanzada que elimina y previene el empañamiento en superficies de vidrio y espejos.</p>
        </div>
        <div class="product">
            <img src="limpiador de cabina.jpg" alt="Producto 2">
            <h4>limpiador de cabina</h4>
            <p>Limpiador de Cabina: Producto eficaz para limpiar y desinfectar el interior del vehículo, dejando un aroma fresco y un acabado impecable.</p>
        </div>
        <div class="product">
            <img src="limpiador de chasis.jpg" alt="Producto 3">
            <h4>limpiador de chasis</h4>
            <p>Limpiador de Chasis: Fórmula potente diseñada para eliminar suciedad, grasa y residuos del chasis, manteniendo la estructura del vehículo en óptimas condiciones.</p>
        </div>
        <div class="product">
            <img src="limpiador de frenos.jpg" alt="Producto 4">
            <h4>limpiador de frenos</h4>
            <p>Limpiador de Frenos: Producto especializado que elimina polvo, aceite y residuos del sistema de frenos, optimizando su rendimiento y seguridad.</p>
        </div>
    </div>
</div>

<footer>
    <div class="container">
        <div class="column">
            <h3>Contacto</h3>
            <p>Dirección: Av. Automotriz 123, Quilmana, Cañete</p>
            <p>Teléfono: (01) 234-5678</p>
            <p>Email: contacto@garajeautomotriz.com</p>
        </div>

        <div class="column">
            <h3>Enlaces Rápidos</h3>
            <a href="#">Política de Privacidad</a>
            <a href="#">Términos de Servicio</a>
            <a href="#">Mapa del Sitio</a>
            <a href="#">Ayuda</a>
        </div>

        <div class="column">
            <h3>Redes Sociales</h3>
            <div class="social-icons">
                <a href="#"><img src="facebooklogo.png" alt="Facebook"></a>
                <a href="#"><img src="twitter1.png" alt="Twitter"></a>
                <a href="#"><img src="instagram1.png" alt="Instagram"></a>
            </div>
        </div>

        <div class="column">
            <h3>Horario de Atención</h3>
            <p>Lunes a Viernes: 8:00 AM - 6:00 PM</p>
            <p>Sábados: 9:00 AM - 1:00 PM</p>
            <p>Domingos: Cerrado</p>
        </div>
    </div>

    <div class="footer-bottom">
        <p>&copy; 2024 Garaje Automotriz - Todos los derechos reservados.</p>
        <p>Diseñado por Garaje Automotriz.</p>
    </div>
</footer>

</body>
</html>
