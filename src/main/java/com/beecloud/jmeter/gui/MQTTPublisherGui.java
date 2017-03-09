package com.beecloud.jmeter.gui;

import com.beecloud.jmeter.constants.Constants;
import com.beecloud.jmeter.sampler.PublisherSampler;
import org.apache.jmeter.gui.util.JLabeledRadioI18N;
import org.apache.jmeter.gui.util.JSyntaxTextArea;
import org.apache.jmeter.gui.util.JTextScrollPane;
import org.apache.jmeter.gui.util.VerticalPanel;
import org.apache.jmeter.samplers.gui.AbstractSamplerGui;
import org.apache.jmeter.testelement.TestElement;
import org.apache.jorphan.gui.JLabeledPasswordField;
import org.apache.jorphan.gui.JLabeledTextField;
import org.apache.jorphan.logging.LoggingManager;

import javax.swing.*;
import java.awt.*;

/**
 * @author hong.lin
 * @description
 * @date 2017/3/8.
 */
public class MQTTPublisherGui extends AbstractSamplerGui {

    private static final org.apache.log.Logger log = LoggingManager.getLoggerForClass();
    private final JLabeledTextField brokerUrlField = new JLabeledTextField(Constants.MQTT_PROVIDER_URL);
    private final JLabeledTextField vehicleInfo = new JLabeledTextField(Constants.MQTT_VEHICLE);
    private final JCheckBox retained = new JCheckBox(Constants.MQTT_SEND_AS_RETAINED_MSG, false);
    private final JCheckBox cleanSession = new JCheckBox(Constants.MQTT_CLEAN_SESSION, false);
    private final JCheckBox autoAuth = new JCheckBox(Constants.MQTT_AUTO_AUTH, false);
    private final JLabeledTextField mqttKeepAlive = new JLabeledTextField(Constants.MQTT_KEEP_ALIVE);

    private final JLabeledTextField mqttUser = new JLabeledTextField(Constants.MQTT_USERNAME);
    private final JLabeledTextField mqttPwd = new JLabeledPasswordField(Constants.MQTT_PASSWORD);


    private final JLabel textArea = new JLabel(Constants.MQTT_TEXT_AREA);
    private final JSyntaxTextArea textMessage = new JSyntaxTextArea(10, 50);
    private final JTextScrollPane textPanel = new JTextScrollPane(textMessage);
    private static final String[] QOS_TYPES_ITEMS = {Constants.MQTT_AT_MOST_ONCE, Constants.MQTT_AT_LEAST_ONCE, Constants.MQTT_EXACTLY_ONCE};
    private final JLabeledRadioI18N typeQoSValue = new JLabeledRadioI18N(Constants.MQTT_QOS, QOS_TYPES_ITEMS, Constants.MQTT_AT_MOST_ONCE);

    public MQTTPublisherGui() {
        initWindow();
    }

    @Override
    public String getLabelResource() {
        return this.getClass().getSimpleName();
    }

    /**
     * Jmeter中显示的Samper名称
     * @return
     */
    @Override
    public String getStaticLabel() {
        return Constants.MQTT_PUBLISHER_TITLE;
    }


    /**
     * 创建元素
     * @return
     */
    @Override
    public TestElement createTestElement() {
        PublisherSampler sampler = new PublisherSampler();
        modifyTestElement(sampler);
        return sampler;
    }


    /**
     * GUI中的值保存到properties中,供samper调用
     * @param testElement
     */
    @Override
    public void modifyTestElement(TestElement testElement) {
        PublisherSampler sampler = (PublisherSampler)testElement;
        super.configureTestElement(sampler);
        sampler.setBrokerUrl(brokerUrlField.getText());
        sampler.setVehicle(vehicleInfo.getText());
        sampler.setUsername(mqttUser.getText());
        sampler.setPassword(mqttPwd.getText());
        sampler.setRetained(retained.isSelected());
        sampler.setCleanSession(cleanSession.isSelected());
        sampler.setAutoAuth(autoAuth.isSelected());
        sampler.setQos(Constants.getQos(typeQoSValue.getText()));
        sampler.setKeepAlive(Integer.parseInt(mqttKeepAlive.getText()));
        sampler.setMessage(textMessage.getText());
    }


    /**
     * 初始化UI界面
     */
    private void initWindow() {
        brokerUrlField.setText(Constants.MQTT_URL_DEFAULT);
        vehicleInfo.setText(Constants.VEHICLE_DEFAULT);
        setLayout(new BorderLayout());
        setBorder(makeBorder());
        add(makeTitlePanel(), BorderLayout.NORTH);
        JPanel mainPanel = new VerticalPanel();
        add(mainPanel, BorderLayout.CENTER);
        JPanel DPanel = new JPanel();
        DPanel.setLayout(new BoxLayout(DPanel, BoxLayout.X_AXIS));
        DPanel.add(brokerUrlField);
        JPanel ControlPanel = new VerticalPanel();
        ControlPanel.add(DPanel);
        ControlPanel.add(createVehiclePane());
        ControlPanel.add(retained);
        ControlPanel.add(cleanSession);
        ControlPanel.add(autoAuth);
        ControlPanel.add(createKeepAlivePane());
        ControlPanel.add(createAuthPane());
        ControlPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.gray),
                "Connect Info"));
        mainPanel.add(ControlPanel);
        JPanel TPanel = new VerticalPanel();
        TPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.gray), "Qos Option"));
        typeQoSValue.setLayout(new BoxLayout(typeQoSValue, BoxLayout.X_AXIS));
        TPanel.add(typeQoSValue);
        mainPanel.add(TPanel);
        JPanel contentPanel = new VerticalPanel();
        JPanel messageContentPanel = new JPanel(new BorderLayout());
        messageContentPanel.add(this.textArea, BorderLayout.NORTH);
        messageContentPanel.add(this.textPanel, BorderLayout.CENTER);
        contentPanel.add(messageContentPanel);

        contentPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.gray), "Content"));
        mainPanel.add(contentPanel);
        brokerUrlField.setText(Constants.MQTT_URL_DEFAULT);

        this.textArea.setVisible(true);
        this.textPanel.setVisible(true);

    }

    /**
     * 创建用户名/密码输入框
     * @return
     */
    private Component createAuthPane() {
        mqttUser.setText(Constants.MQTT_USER_USERNAME);
        mqttPwd.setText(Constants.MQTT_USER_PASSWORD);
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
        panel.add(Box.createHorizontalStrut(10));
        panel.add(mqttUser);
        panel.add(Box.createHorizontalStrut(10));
        panel.add(mqttPwd);
        panel.add(Box.createHorizontalStrut(10));
        return panel;
    }


    /**
     * 从保存的文件中加载配置（Jmeter保存配置的功能）
     * @param el
     */
    @Override
    public void configure(TestElement el) {
        super.configure(el);
        PublisherSampler sampler = (PublisherSampler) el;
        brokerUrlField.setText(sampler.getBrokerUrl());
        vehicleInfo.setText(sampler.getVehicle());
        retained.setSelected(sampler.getRetained());
        autoAuth.setSelected(sampler.getAutoAuth());
        cleanSession.setSelected(sampler.getCleanSession());
        mqttKeepAlive.setText(sampler.getKeepAlive()+"");
        mqttUser.setText(sampler.getUsername());
        mqttPwd.setText(sampler.getPassword());
        textMessage.setText(sampler.getMessage());
    }


    @Override
    public void clearGui() {
        super.clearGui();
    }


    /**
     * 创建输入Vehicle输入框
     * @return
     */
    private JPanel createVehiclePane() {
        JPanel panel = new VerticalPanel(); //new BorderLayout(3, 0)
        vehicleInfo.setLabel("Vehicle");
        vehicleInfo.setLayout((new BoxLayout(vehicleInfo, BoxLayout.X_AXIS)));
        panel.add(vehicleInfo);
        JPanel TPanel = new JPanel();
        TPanel.setLayout(new BoxLayout(TPanel, BoxLayout.X_AXIS));
        TPanel.add(Box.createHorizontalStrut(100));
        panel.add(TPanel);
        return panel;
    }

    /**
     * 创建keepAlive输入框
     * @return
     */
    private JPanel createKeepAlivePane() {
        JPanel panel = new VerticalPanel();
        this.mqttKeepAlive.setLayout((new BoxLayout(mqttKeepAlive, BoxLayout.X_AXIS)));
        panel.add(mqttKeepAlive);
        JPanel TPanel = new JPanel();
        TPanel.setLayout(new BoxLayout(TPanel, BoxLayout.X_AXIS));
        TPanel.add(Box.createHorizontalStrut(100));
        panel.add(TPanel);
        mqttKeepAlive.setText(Constants.MQTT_KEEP_ALIVE_DEFAULT);
        return panel;
    }
}
