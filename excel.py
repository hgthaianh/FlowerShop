import pandas as pd
import re
import io

# Define the structure based on the user's template
columns = [
    "ID", "Items", "Sub-items", "Description", "PreCondition", 
    "Steps to Excute", "Expected output", "Test Data/Parameters", 
    "IE11", "Edge", "Chrome", "Safari", "IphoneX", "Samsung S7", "Ipad", "HTC Tablet", 
    "D", "Date", "BugID", "Note"
]

# Raw data parsing logic
# Since the input is large, I will construct the list of dictionaries programmatically based on the provided markdown text logic.

data = []

def add_tc(tc_id, items, sub_items, desc, pre_cond, steps, exp_out, test_data, note_type, note_detail):
    data.append({
        "ID": tc_id,
        "Items": items,
        "Sub-items": sub_items,
        "Description": desc,
        "PreCondition": pre_cond,
        "Steps to Excute": steps,
        "Expected output": exp_out,
        "Test Data/Parameters": test_data,
        "IE11": "", "Edge": "", "Chrome": "", "Safari": "", 
        "IphoneX": "", "Samsung S7": "", "Ipad": "", "HTC Tablet": "", 
        "D": "",
        "Date": "2025-12-31",
        "BugID": "",
        "Note": f"{note_type} - {note_detail}"
    })

# --- 1. Login ---
item = "Authentication"
sub = "Login"
endpoint = "POST /api/auth/login"
pre = "Application Server is running"

add_tc("TC_LOGIN_01", item, sub, "Đăng nhập thành công với thông tin hợp lệ", pre, f"1. Call {endpoint}", "Status 200, trả về token JWT", 'email: "user@example.com", password: "Password123"', "Positive", "Happy path")
add_tc("TC_LOGIN_02", item, sub, "Đăng nhập với email trống", pre, f"1. Call {endpoint}", 'Status 400, message: "Email is required"', 'email: "", password: "Password123"', "Negative", "Validation")
add_tc("TC_LOGIN_03", item, sub, "Đăng nhập với password trống", pre, f"1. Call {endpoint}", 'Status 400, message: "Password is required"', 'email: "user@example.com", password: ""', "Negative", "Validation")
add_tc("TC_LOGIN_04", item, sub, "Đăng nhập với email không đúng định dạng", pre, f"1. Call {endpoint}", 'Status 400, message: "Invalid email format"', 'email: "invalid-email", password: "Password123"', "Negative", "Validation")
add_tc("TC_LOGIN_05", item, sub, "Đăng nhập với email không tồn tại", pre, f"1. Call {endpoint}", 'Status 401, message: "Invalid credentials"', 'email: "notexist@example.com", password: "Password123"', "Negative", "Authentication")
add_tc("TC_LOGIN_06", item, sub, "Đăng nhập với mật khẩu sai", pre, f"1. Call {endpoint}", 'Status 401, message: "Invalid credentials"', 'email: "user@example.com", password: "WrongPassword"', "Negative", "Authentication")
add_tc("TC_LOGIN_07", item, sub, "Đăng nhập với email chứa khoảng trắng", pre, f"1. Call {endpoint}", "Status 200 hoặc 400 tùy xử lý trim", 'email: " user@example.com ", password: "Password123"', "Boundary", "Edge case")
add_tc("TC_LOGIN_08", item, sub, "Đăng nhập với request body rỗng", pre, f"1. Call {endpoint}", 'Status 400, message: "Email and password are required"', '{}', "Negative", "Validation")
add_tc("TC_LOGIN_09", item, sub, "Đăng nhập với email có ký tự đặc biệt hợp lệ", pre, f"1. Call {endpoint}", "Status 200 hoặc 401", 'email: "user+test@example.com", password: "Password123"', "Positive", "Special chars")
add_tc("TC_LOGIN_10", item, sub, "Đăng nhập khi server database không hoạt động", "Database is down", f"1. Call {endpoint}", 'Status 500, message: "Internal Server Error"', 'email: "user@example.com", password: "Password123"', "Negative", "Error handling")

# --- 2. Register ---
sub = "Register"
endpoint = "POST /api/auth/signup"

add_tc("TC_SIGNUP_01", item, sub, "Đăng ký thành công với thông tin hợp lệ", pre, f"1. Call {endpoint}", 'Status 201, message: "User registered successfully"', 'name: "Nguyen Van A", email: "newuser@example.com", password: "Password123"', "Positive", "Happy path")
add_tc("TC_SIGNUP_02", item, sub, "Đăng ký với email đã tồn tại", pre, f"1. Call {endpoint}", 'Status 400, message: "Email address already in use"', 'name: "Test User", email: "existing@example.com", password: "Password123"', "Negative", "Validation")
add_tc("TC_SIGNUP_03", item, sub, "Đăng ký với name trống", pre, f"1. Call {endpoint}", 'Status 400, message: "Name is required"', 'name: "", email: "new@example.com", password: "Password123"', "Negative", "Validation")
add_tc("TC_SIGNUP_04", item, sub, "Đăng ký với email trống", pre, f"1. Call {endpoint}", 'Status 400, message: "Email is required"', 'name: "Test User", email: "", password: "Password123"', "Negative", "Validation")
add_tc("TC_SIGNUP_05", item, sub, "Đăng ký với password trống", pre, f"1. Call {endpoint}", 'Status 400, message: "Password is required"', 'name: "Test User", email: "new@example.com", password: ""', "Negative", "Validation")
add_tc("TC_SIGNUP_06", item, sub, "Đăng ký với email không đúng định dạng", pre, f"1. Call {endpoint}", 'Status 400, message: "Invalid email format"', 'name: "Test User", email: "invalid-email", password: "Password123"', "Negative", "Validation")
add_tc("TC_SIGNUP_07", item, sub, "Đăng ký với tên quá dài (>255 ký tự)", pre, f"1. Call {endpoint}", 'Status 400, message: "Name is too long"', 'name: "A" * 300, email: "new@example.com", password: "Password123"', "Boundary", "Edge case")
add_tc("TC_SIGNUP_08", item, sub, "Đăng ký với tất cả các trường rỗng", pre, f"1. Call {endpoint}", "Status 400, danh sách các lỗi validation", 'name: "", email: "", password: ""', "Negative", "Validation")
add_tc("TC_SIGNUP_09", item, sub, "Đăng ký với email viết hoa", pre, f"1. Call {endpoint}", "Status 201, email được lưu lowercase hoặc giữ nguyên", 'name: "Test User", email: "NEW@EXAMPLE.COM", password: "Password123"', "Positive", "Case handling")
add_tc("TC_SIGNUP_10", item, sub, "Đăng ký với name chứa ký tự Unicode", pre, f"1. Call {endpoint}", "Status 201, name được lưu đúng", 'name: "Nguyễn Văn Á", email: "unicode@example.com", password: "Password123"', "Positive", "Unicode support")

# --- 3. Product Management ---
item = "Product Management"
pre = "Admin logged in, valid Token"

# 3.1 Create
sub = "Create Product"
endpoint = "POST /api/products"
add_tc("TC_PROD_CREATE_01", item, sub, "Tạo sản phẩm thành công với thông tin hợp lệ", pre, f"1. Call {endpoint}", "Status 201, trả về product object", 'productCode: "SP001", name: "Hoa Hồng Đỏ", price: 150000, categoryId: 1', "Positive", "Happy path")
add_tc("TC_PROD_CREATE_02", item, sub, "Tạo sản phẩm với productCode trống", pre, f"1. Call {endpoint}", 'Status 400, message: "Product code is required"', 'productCode: "", name: "Hoa Test", price: 100000, categoryId: 1', "Negative", "Validation")
add_tc("TC_PROD_CREATE_03", item, sub, "Tạo sản phẩm với name trống", pre, f"1. Call {endpoint}", 'Status 400, message: "Name is required"', 'productCode: "SP002", name: "", price: 100000, categoryId: 1', "Negative", "Validation")
add_tc("TC_PROD_CREATE_04", item, sub, "Tạo sản phẩm với price âm", pre, f"1. Call {endpoint}", 'Status 400, message: "Price must be greater than or equal to zero"', 'productCode: "SP003", name: "Hoa Test", price: -50000, categoryId: 1', "Negative", "Validation")
add_tc("TC_PROD_CREATE_05", item, sub, "Tạo sản phẩm với categoryId không tồn tại", pre, f"1. Call {endpoint}", 'Status 404, message: "Category not found"', 'productCode: "SP004", name: "Hoa Test", price: 100000, categoryId: 9999', "Negative", "Not found")
add_tc("TC_PROD_CREATE_06", item, sub, "Tạo sản phẩm với description quá dài", pre, f"1. Call {endpoint}", 'Status 400, message: "Description must not exceed 2000 characters"', 'description: "A" * 2500', "Boundary", "Validation")
add_tc("TC_PROD_CREATE_07", item, sub, "Tạo sản phẩm với price = 0", pre, f"1. Call {endpoint}", "Status 201, product được tạo thành công", 'price: 0', "Boundary", "Edge case")
add_tc("TC_PROD_CREATE_08", item, sub, "Tạo sản phẩm với imageUrl hợp lệ", pre, f"1. Call {endpoint}", "Status 201, product với image", 'imageUrl: "https://example.com/image.jpg"', "Positive", "With image")
add_tc("TC_PROD_CREATE_09", item, sub, "Tạo sản phẩm với nhiều imageUrls", pre, f"1. Call {endpoint}", "Status 201, product với multiple images", 'imageUrls: ["url1", "url2"]', "Positive", "Multiple images")
add_tc("TC_PROD_CREATE_10", item, sub, "Tạo sản phẩm mà không có authentication", "No Auth", f"1. Call {endpoint}", 'Status 401, message: "Unauthorized"', '(No Auth Token)', "Negative", "Security")

# 3.2 Update
sub = "Update Product"
endpoint = "PUT /api/products/{id}"
add_tc("TC_PROD_UPDATE_01", item, sub, "Cập nhật sản phẩm thành công", pre, f"1. Call {endpoint}", "Status 200, product được cập nhật", 'id: 1, name: "Hoa Hồng Mới", price: 200000', "Positive", "Happy path")
add_tc("TC_PROD_UPDATE_02", item, sub, "Cập nhật sản phẩm không tồn tại", pre, f"1. Call {endpoint}", 'Status 404, message: "Product not found"', 'id: 99999, name: "Test"', "Negative", "Not found")
add_tc("TC_PROD_UPDATE_03", item, sub, "Cập nhật với price âm", pre, f"1. Call {endpoint}", 'Status 400, message: "Price must be greater than or equal to zero"', 'id: 1, price: -10000', "Negative", "Validation")
add_tc("TC_PROD_UPDATE_04", item, sub, "Cập nhật với name trống", pre, f"1. Call {endpoint}", 'Status 400, message: "Name is required"', 'id: 1, name: ""', "Negative", "Validation")
add_tc("TC_PROD_UPDATE_05", item, sub, "Cập nhật categoryId không tồn tại", pre, f"1. Call {endpoint}", 'Status 404, message: "Category not found"', 'id: 1, categoryId: 9999', "Negative", "Not found")
add_tc("TC_PROD_UPDATE_06", item, sub, "Cập nhật với description hợp lệ (2000 ký tự)", pre, f"1. Call {endpoint}", "Status 200, product được cập nhật", 'id: 1, description: "A" * 2000', "Boundary", "Max length")
add_tc("TC_PROD_UPDATE_07", item, sub, "Cập nhật không có authentication", "No Auth", f"1. Call {endpoint}", 'Status 401, message: "Unauthorized"', '(No Auth Token)', "Negative", "Security")

# 3.3 Delete
sub = "Delete Product"
endpoint = "DELETE /api/products/{id}"
add_tc("TC_PROD_DELETE_01", item, sub, "Xóa sản phẩm thành công", pre, f"1. Call {endpoint}", "Status 204, No Content", 'id: 1', "Positive", "Happy path")
add_tc("TC_PROD_DELETE_02", item, sub, "Xóa sản phẩm không tồn tại", pre, f"1. Call {endpoint}", 'Status 404, message: "Product not found"', 'id: 99999', "Negative", "Not found")
add_tc("TC_PROD_DELETE_03", item, sub, "Xóa sản phẩm với id không hợp lệ", pre, f"1. Call {endpoint}", 'Status 400, message: "Invalid product ID"', 'id: "abc"', "Negative", "Validation")
add_tc("TC_PROD_DELETE_04", item, sub, "Xóa sản phẩm với id âm", pre, f"1. Call {endpoint}", 'Status 400, message: "Invalid product ID"', 'id: -1', "Negative", "Validation")
add_tc("TC_PROD_DELETE_05", item, sub, "Xóa sản phẩm đang có trong đơn hàng", pre, f"1. Call {endpoint}", "Status 400 hoặc 200 (soft delete)", 'id: 5 (product in active order)', "Negative", "Constraint")
add_tc("TC_PROD_DELETE_06", item, sub, "Xóa không có authentication", "No Auth", f"1. Call {endpoint}", 'Status 401, message: "Unauthorized"', '(No Auth Token)', "Negative", "Security")
add_tc("TC_PROD_DELETE_07", item, sub, "Xóa hai lần cùng một sản phẩm", pre, f"1. Call {endpoint}", 'Status 404, message: "Product not found"', 'id: 1 (đã xóa trước đó)', "Negative", "Idempotency")

# 3.4 List
sub = "List Products"
endpoint = "GET /api/products"
pre_list = "Server running"
add_tc("TC_PROD_LIST_01", item, sub, "Lấy tất cả sản phẩm", pre_list, f"1. Call {endpoint}", "Status 200, danh sách products", '(No params)', "Positive", "Happy path")
add_tc("TC_PROD_LIST_02", item, sub, "Tìm kiếm sản phẩm theo tên", pre_list, f"1. Call {endpoint}", "Status 200, danh sách products matching", 'search: "Hoa Hồng"', "Positive", "Search")
add_tc("TC_PROD_LIST_03", item, sub, "Tìm kiếm không có kết quả", pre_list, f"1. Call {endpoint}", "Status 200, danh sách rỗng []", 'search: "XYZ123"', "Positive", "Empty result")
add_tc("TC_PROD_LIST_04", item, sub, "Tìm kiếm với chuỗi rỗng", pre_list, f"1. Call {endpoint}", "Status 200, tất cả products", 'search: ""', "Boundary", "Edge case")
add_tc("TC_PROD_LIST_05", item, sub, "Tìm kiếm với ký tự đặc biệt", pre_list, f"1. Call {endpoint}", "Status 200, xử lý đúng special chars", 'search: "Hoa %@#"', "Boundary", "Special chars")
add_tc("TC_PROD_LIST_06", item, sub, "Lấy danh sách khi không có sản phẩm", "DB Empty", f"1. Call {endpoint}", "Status 200, danh sách rỗng []", '(Database empty)', "Boundary", "Empty DB")
add_tc("TC_PROD_LIST_07", item, sub, "Tìm kiếm với chuỗi chỉ có khoảng trắng", pre_list, f"1. Call {endpoint}", "Status 200, tất cả products (treated as empty)", 'search: "   "', "Boundary", "Whitespace")

# --- 4. Order Management ---
item = "Order Management"
pre = "User/Admin logged in"

# 4.1 Create
sub = "Create Order"
endpoint = "POST /api/orders"
add_tc("TC_ORDER_CREATE_01", item, sub, "Tạo đơn hàng thành công", pre, f"1. Call {endpoint}", "Status 200, order được tạo với status PENDING", 'customerName: "Nguyen Van A", ... items: [{productId: 1, quantity: 2}]', "Positive", "Happy path")
add_tc("TC_ORDER_CREATE_02", item, sub, "Tạo đơn hàng với customerName trống", pre, f"1. Call {endpoint}", 'Status 400, message: "Customer name is required"', 'customerName: ""', "Negative", "Validation")
add_tc("TC_ORDER_CREATE_03", item, sub, "Tạo đơn hàng với shippingAddress trống", pre, f"1. Call {endpoint}", 'Status 400, message: "Shipping address is required"', 'shippingAddress: ""', "Negative", "Validation")
add_tc("TC_ORDER_CREATE_04", item, sub, "Tạo đơn hàng với phoneNumber trống", pre, f"1. Call {endpoint}", 'Status 400, message: "Phone number is required"', 'phoneNumber: ""', "Negative", "Validation")
add_tc("TC_ORDER_CREATE_05", item, sub, "Tạo đơn hàng với items rỗng", pre, f"1. Call {endpoint}", 'Status 400, message: "Items list cannot be empty"', 'items: []', "Negative", "Validation")
add_tc("TC_ORDER_CREATE_06", item, sub, "Tạo đơn hàng với productId không tồn tại", pre, f"1. Call {endpoint}", 'Status 404, message: "Product not found"', 'items: [{productId: 99999, quantity: 1}]', "Negative", "Not found")
add_tc("TC_ORDER_CREATE_07", item, sub, "Tạo đơn hàng với quantity = 0", pre, f"1. Call {endpoint}", 'Status 400, message: "Quantity must be greater than 0"', 'quantity: 0', "Negative", "Validation")
add_tc("TC_ORDER_CREATE_08", item, sub, "Tạo đơn hàng với quantity âm", pre, f"1. Call {endpoint}", 'Status 400, message: "Quantity must be greater than 0"', 'quantity: -5', "Negative", "Validation")
add_tc("TC_ORDER_CREATE_09", item, sub, "Tạo đơn hàng với nhiều sản phẩm", pre, f"1. Call {endpoint}", "Status 200, order với multiple items", 'items: multiple', "Positive", "Multiple items")
add_tc("TC_ORDER_CREATE_10", item, sub, "Tạo đơn hàng khi đã đăng nhập", pre, f"1. Call {endpoint}", "Status 200, order linked to user", '(With Auth Token)', "Positive", "Authenticated")

# 4.2 My Orders
sub = "My Orders"
endpoint = "GET /api/orders/my-orders"
add_tc("TC_ORDER_MYORDERS_01", item, sub, "Xem đơn hàng khi đã đăng nhập", pre, f"1. Call {endpoint}", "Status 200, danh sách orders của user", '(With Auth Token)', "Positive", "Happy path")
add_tc("TC_ORDER_MYORDERS_02", item, sub, "Xem đơn hàng khi chưa đăng nhập", "No Auth", f"1. Call {endpoint}", 'Status 401, message: "Unauthorized"', '(No Auth Token)', "Negative", "Security")
add_tc("TC_ORDER_MYORDERS_03", item, sub, "Xem đơn hàng khi user không có order nào", pre, f"1. Call {endpoint}", "Status 200, danh sách rỗng []", '(With Auth Token, new user)', "Positive", "Empty result")
add_tc("TC_ORDER_MYORDERS_04", item, sub, "Xem đơn hàng với token hết hạn", "Token expired", f"1. Call {endpoint}", 'Status 401, message: "Token expired"', '(With Expired Token)', "Negative", "Security")
add_tc("TC_ORDER_MYORDERS_05", item, sub, "Xem đơn hàng với token không hợp lệ", "Token invalid", f"1. Call {endpoint}", 'Status 401, message: "Invalid token"', '(With Invalid Token)', "Negative", "Security")
add_tc("TC_ORDER_MYORDERS_06", item, sub, "Kiểm tra thứ tự sắp xếp theo thời gian", pre, f"1. Call {endpoint}", "Status 200, orders sorted by createdAt DESC", '(With Auth Token)', "Positive", "Sorting")
add_tc("TC_ORDER_MYORDERS_07", item, sub, "Xem chi tiết đơn hàng có items đầy đủ", pre, f"1. Call {endpoint}", "Status 200, mỗi order có items với product info", '(With Auth Token)', "Positive", "Data integrity")

# 4.3 Detail
sub = "Order Detail"
endpoint = "GET /api/orders/{id}"
add_tc("TC_ORDER_DETAIL_01", item, sub, "Xem đơn hàng tồn tại", pre, f"1. Call {endpoint}", "Status 200, order details", 'id: 1', "Positive", "Happy path")
add_tc("TC_ORDER_DETAIL_02", item, sub, "Xem đơn hàng không tồn tại", pre, f"1. Call {endpoint}", 'Status 404, message: "Order not found"', 'id: 99999', "Negative", "Not found")
add_tc("TC_ORDER_DETAIL_03", item, sub, "Xem đơn hàng với id không hợp lệ", pre, f"1. Call {endpoint}", 'Status 400, message: "Invalid order ID"', 'id: "abc"', "Negative", "Validation")
add_tc("TC_ORDER_DETAIL_04", item, sub, "Xem đơn hàng với id âm", pre, f"1. Call {endpoint}", 'Status 400, message: "Invalid order ID"', 'id: -1', "Negative", "Validation")
add_tc("TC_ORDER_DETAIL_05", item, sub, "Kiểm tra order có đầy đủ items", pre, f"1. Call {endpoint}", "Status 200, order.items không null", 'id: 1', "Positive", "Data integrity")
add_tc("TC_ORDER_DETAIL_06", item, sub, "Kiểm tra totalPrice tính đúng", pre, f"1. Call {endpoint}", "Status 200, totalPrice = sum(item.price * item.quantity)", 'id: 1', "Positive", "Calculation")
add_tc("TC_ORDER_DETAIL_07", item, sub, "Xem đơn hàng với id rất lớn", pre, f"1. Call {endpoint}", "Status 404 hoặc 400", 'id: 9999999999', "Boundary", "Large ID")

# --- 5. Category Management ---
item = "Category Management"
pre = "Admin logged in"

# 5.1 Create
sub = "Create Category"
endpoint = "POST /api/categories"
add_tc("TC_CAT_CREATE_01", item, sub, "Tạo danh mục thành công", pre, f"1. Call {endpoint}", "Status 201, category được tạo", 'name: "Hoa Tươi"', "Positive", "Happy path")
add_tc("TC_CAT_CREATE_02", item, sub, "Tạo danh mục với name trống", pre, f"1. Call {endpoint}", 'Status 400, message: "Name is required"', 'name: ""', "Negative", "Validation")
add_tc("TC_CAT_CREATE_03", item, sub, "Tạo danh mục với name chỉ có khoảng trắng", pre, f"1. Call {endpoint}", 'Status 400, message: "Name is required"', 'name: "   "', "Negative", "Validation")
add_tc("TC_CAT_CREATE_04", item, sub, "Tạo danh mục với name trùng lặp", pre, f"1. Call {endpoint}", "Status 400 hoặc 201 tùy business logic", 'name: "Hoa Tươi" (đã tồn tại)', "Boundary", "Duplicate")
add_tc("TC_CAT_CREATE_05", item, sub, "Tạo danh mục với tên tiếng Việt", pre, f"1. Call {endpoint}", "Status 201, tên được lưu đúng", 'name: "Hoa Hồng Đỏ"', "Positive", "Unicode")
add_tc("TC_CAT_CREATE_06", item, sub, "Tạo danh mục với tên quá dài", pre, f"1. Call {endpoint}", 'Status 400, message: "Name is too long"', 'name: "A" * 500', "Boundary", "Max length")
add_tc("TC_CAT_CREATE_07", item, sub, "Tạo danh mục không có authentication", "No Auth", f"1. Call {endpoint}", 'Status 401, message: "Unauthorized"', '(No Auth Token)', "Negative", "Security")

# 5.2 Update
sub = "Update Category"
endpoint = "PUT /api/categories/{id}"
add_tc("TC_CAT_UPDATE_01", item, sub, "Cập nhật danh mục thành công", pre, f"1. Call {endpoint}", "Status 200, category được cập nhật", 'id: 1, name: "Hoa Tươi Mới"', "Positive", "Happy path")
add_tc("TC_CAT_UPDATE_02", item, sub, "Cập nhật danh mục không tồn tại", pre, f"1. Call {endpoint}", 'Status 404, message: "Category not found"', 'id: 99999', "Negative", "Not found")
add_tc("TC_CAT_UPDATE_03", item, sub, "Cập nhật với name trống", pre, f"1. Call {endpoint}", 'Status 400, message: "Name is required"', 'name: ""', "Negative", "Validation")
add_tc("TC_CAT_UPDATE_04", item, sub, "Cập nhật với id không hợp lệ", pre, f"1. Call {endpoint}", 'Status 400, message: "Invalid category ID"', 'id: "abc"', "Negative", "Validation")
add_tc("TC_CAT_UPDATE_05", item, sub, "Cập nhật với id âm", pre, f"1. Call {endpoint}", 'Status 400, message: "Invalid category ID"', 'id: -1', "Negative", "Validation")
add_tc("TC_CAT_UPDATE_06", item, sub, "Cập nhật thành tên đã tồn tại", pre, f"1. Call {endpoint}", "Status 400 hoặc 200 tùy business logic", 'id: 1, name: "Hoa Khô"', "Boundary", "Duplicate")
add_tc("TC_CAT_UPDATE_07", item, sub, "Cập nhật không có authentication", "No Auth", f"1. Call {endpoint}", 'Status 401, message: "Unauthorized"', '(No Auth Token)', "Negative", "Security")

# 5.3 Delete
sub = "Delete Category"
endpoint = "DELETE /api/categories/{id}"
add_tc("TC_CAT_DELETE_01", item, sub, "Xóa danh mục thành công", pre, f"1. Call {endpoint}", "Status 204, No Content", 'id: 1', "Positive", "Happy path")
add_tc("TC_CAT_DELETE_02", item, sub, "Xóa danh mục không tồn tại", pre, f"1. Call {endpoint}", 'Status 404, message: "Category not found"', 'id: 99999', "Negative", "Not found")
add_tc("TC_CAT_DELETE_03", item, sub, "Xóa danh mục có sản phẩm", pre, f"1. Call {endpoint}", 'Status 400, message: "Cannot delete category with products"', 'id: 2', "Negative", "Constraint")
add_tc("TC_CAT_DELETE_04", item, sub, "Xóa với id không hợp lệ", pre, f"1. Call {endpoint}", 'Status 400, message: "Invalid category ID"', 'id: "abc"', "Negative", "Validation")
add_tc("TC_CAT_DELETE_05", item, sub, "Xóa với id âm", pre, f"1. Call {endpoint}", 'Status 400, message: "Invalid category ID"', 'id: -1', "Negative", "Validation")
add_tc("TC_CAT_DELETE_06", item, sub, "Xóa hai lần cùng một danh mục", pre, f"1. Call {endpoint}", 'Status 404, message: "Category not found"', 'id: 1 (đã xóa)', "Negative", "Idempotency")
add_tc("TC_CAT_DELETE_07", item, sub, "Xóa không có authentication", "No Auth", f"1. Call {endpoint}", 'Status 401, message: "Unauthorized"', '(No Auth Token)', "Negative", "Security")

# 5.4 List
sub = "List Categories"
endpoint = "GET /api/categories"
pre_list = "Server running"
add_tc("TC_CAT_LIST_01", item, sub, "Lấy tất cả danh mục", pre_list, f"1. Call {endpoint}", "Status 200, danh sách categories", '(No params)', "Positive", "Happy path")
add_tc("TC_CAT_LIST_02", item, sub, "Lấy danh mục khi database rỗng", "DB Empty", f"1. Call {endpoint}", "Status 200, danh sách rỗng []", '(Database empty)', "Boundary", "Empty DB")
add_tc("TC_CAT_LIST_03", item, sub, "Kiểm tra cấu trúc response", pre_list, f"1. Call {endpoint}", "Status 200, mỗi category có id, name", '(No params)', "Positive", "Data structure")
add_tc("TC_CAT_LIST_04", item, sub, "Lấy danh mục cụ thể theo ID", pre_list, f"1. Call {endpoint}/{id}", "Status 200, single category object", 'id: 1', "Positive", "Get by ID")
add_tc("TC_CAT_LIST_05", item, sub, "Lấy danh mục không tồn tại theo ID", pre_list, f"1. Call {endpoint}/{id}", 'Status 404, message: "Category not found"', 'id: 99999', "Negative", "Not found")
add_tc("TC_CAT_LIST_06", item, sub, "Lấy danh mục với ID không hợp lệ", pre_list, f"1. Call {endpoint}/{id}", 'Status 400, message: "Invalid category ID"', 'id: "abc"', "Negative", "Validation")
add_tc("TC_CAT_LIST_07", item, sub, "Kiểm tra performance", "Large DB", f"1. Call {endpoint}", "Status 200, response time < 2s", '(100+ categories)', "Positive", "Performance")

# Create DataFrame
df = pd.DataFrame(data, columns=columns)

# Save to Excel
output_path = 'flower_shop_testcases.xlsx'
df.to_excel(output_path, index=False)

print(output_path)