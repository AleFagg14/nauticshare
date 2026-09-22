Properties props = new Properties();
InputStream input = getClass().getClassLoader()
        .getResourceAsStream("db.properties");
props.load(input);
String url      = props.getProperty("db.url");
String username = props.getProperty("db.username");
String password = props.getProperty("db.password");