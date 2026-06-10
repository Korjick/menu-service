<h3>Menu Service</h3>
<h4>Provides a REST API for CRUD operations on menu items:</h4>
<ul class="mt-2">
<li class="mt-2"><i>POST /v1/menu-items</i> - create a menu item; item details are passed in the request body. Available to staff members; staff information is passed in the access token. 
</li>
<li class="mt-2"><i>DELETE /v1/menu-items/{id}</i> - delete a menu item. Available to staff members; staff information is passed in the access token.</li>
<li class="mt-2"><i>PATCH /v1/menu-items/{id}</i> - update a menu item; update parameters are passed in the request body. Available to staff members; staff information is passed in the access token.
</li>
<li class="mt-2"><i>GET /v1/menu-items/{id}</i> - retrieve a menu item. Available to all users.</li>
<li class="mt-2"><i>GET /v1/menu-items?category={category}&sort={sort}</i> - retrieve a list of menu items from a selected category, sorted alphabetically (AZ, ZA), by price (PRICE_ASC, PRICE_DESC), or by creation date (DATE_ASC, DATE_DESC). Available to all users.
</li>
</ul>
Data is stored in a PostgreSQL 16 relational database.