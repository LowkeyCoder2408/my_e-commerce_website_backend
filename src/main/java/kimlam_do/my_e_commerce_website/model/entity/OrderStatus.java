package kimlam_do.my_e_commerce_website.model.entity;

public enum OrderStatus {
    NEW {
        @Override
        public String defaultDescription() {
            return "Vừa khởi tạo";
        }
    },

    PROCESSING {
        @Override
        public String defaultDescription() {
            return "Đang xử lý";
        }
    },

    PACKAGED {
        @Override
        public String defaultDescription() {
            return "Đã đóng gói";
        }
    },

    PICKED {
        @Override
        public String defaultDescription() {
            return "Shipper đã nhận hàng";
        }
    },

    SHIPPING {
        @Override
        public String defaultDescription() {
            return "Đang giao hàng";
        }
    },

    DELIVERED {
        @Override
        public String defaultDescription() {
            return "Đã được giao";
        }
    },

    PAID {
        @Override
        public String defaultDescription() {
            return "Đã thanh toán";
        }
    },

    CANCELED {
        @Override
        public String defaultDescription() {
            return "Đã bị hủy";
        }
    },

    RETURN_REQUESTED {
        @Override
        public String defaultDescription() {
            return "Yêu cầu hoàn trả";
        }
    },

    RETURNED {
        @Override
        public String defaultDescription() {
            return "Đã hoàn trả";
        }
    },

    REFUNDED {
        @Override
        public String defaultDescription() {
            return "Đã hoàn tiền";
        }
    };

    public abstract String defaultDescription();
}