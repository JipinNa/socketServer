import com.example.chioy.test.PersonInfo;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

public class SocketServer {
    public static void main(String[] args) throws IOException, SQLException, ClassNotFoundException {
        MysqlConnection mysqlConnection = new MysqlConnection();
        ArrayList<PersonInfo> personInfos = new ArrayList<>();
        ServerSocket server = new ServerSocket(3234);
        AtomicBoolean connected = new AtomicBoolean(false);

        //连接数据库
        mysqlConnection.setConn();
        if(!mysqlConnection.getConn().isClosed()){
            System.out.println("数据库连接成功");
        }else {
            System.out.println("数据库连接失败");
        }
       //查询数据库里数据
        //queryDb(mysqlConnection,personInfos);
        //服务器等待连接
        AtomicReference<Socket> socket = new AtomicReference<>(server.accept());
        System.out.println("启动服务器");

        new Thread(() -> {
            ObjectOutputStream objectOutputStream = null;
            try {
                objectOutputStream = new ObjectOutputStream(socket.get().getOutputStream());
                //sendData(objectOutputStream, personInfos);
            }
            catch (IOException e) {
            }
            while (true) {
                // 读取数据
                InputStream inputStream = null;
                try {
                    System.out.println("充能完毕！");
                    //sendData(objectOutputStream, personInfos);
                    System.out.println("等待命令！");
                    inputStream = socket.get().getInputStream();
                    System.out.println("准备发射！");
                    BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);
                    ObjectInputStream objectInputStream = new ObjectInputStream(bufferedInputStream);
                    Object obj = objectInputStream.readObject();
                    if(obj != null) {
                        PersonInfo transportInfo = (PersonInfo) obj;
                        System.out.println("接收命令！");
                        switch (transportInfo.getInit()){
                               case 1:
                                   mysqlConnection.submitPatientInfo(transportInfo);
                                    queryDb(mysqlConnection, personInfos);
                                    System.out.println("疯狂输出！");
                                    sendData(objectOutputStream, personInfos);
                                    break;
                                case 2:
                                    mysqlConnection.updatePatientInfo(transportInfo);
                                    queryDb(mysqlConnection, personInfos);
                                    System.out.println("疯狂输出！");
                                    sendData(objectOutputStream, personInfos);
                                    break;
                                case 3:
                                    queryDb(mysqlConnection, personInfos);
                                    sendData(objectOutputStream,personInfos);
                                    System.out.println("疯狂输出！");
                                    break;
                            }
                            System.out.println("等待下一波命令！");
                        }
                } catch (IOException e) {
                    try {
                        socket.get().close();
                        System.out.println("socket关闭！");
                        connected.set(true);
                        while (connected.get()) {
                            System.out.println("socket等待循环！");
                            if (socket.get().isClosed()) {
                                System.out.println("socket等待连接！");
                                socket.set(server.accept());
                                objectOutputStream = new ObjectOutputStream(socket.get().getOutputStream());
                                if (socket.get().isConnected()) {
                                    System.out.println("socket连接成功，跳出循环！");
                                    connected.set(false);
                                }
                            }
                        }

                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public static void queryDb(MysqlConnection mysqlConnection,ArrayList<PersonInfo> personInfos) throws SQLException {
        int i = 0;
        personInfos.clear();
        ResultSet resultSet = mysqlConnection.getResultSet("select * from patient_info");
        while (resultSet.next()){
            personInfos.add(i,new PersonInfo(resultSet.getInt("patient_id"),
                    resultSet.getString("patient_name"),
                    resultSet.getString("patient_sex"),
                    resultSet.getString("patient_age"),
                    resultSet.getDouble("patient_height"),
                    resultSet.getDouble("patient_weight"),
                    resultSet.getInt("patient_init")));
            i++;
            System.out.println("ID :"+i);
        }
    }

    private static void sendData(ObjectOutputStream objectOutputStream,ArrayList<PersonInfo> objects) throws IOException {
        //发送数据
        objectOutputStream.reset();
        objectOutputStream.writeObject(objects);
        objectOutputStream.flush();
    }

}
