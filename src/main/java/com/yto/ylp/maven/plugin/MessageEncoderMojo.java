package com.yto.ylp.maven.plugin;

import com.yto.ylp.maven.utils.CustomEncoder;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

/**
 * 一个将 messages.properties value转换为 ASCII 并替换源文件的 Mojo。
 */
@Mojo(name = "encode", defaultPhase = LifecyclePhase.PROCESS_RESOURCES)
public class MessageEncoderMojo extends AbstractMojo {

	/**
	 * 要进行 ASCII 编码的源文件路径，相对于项目根目录。
	 */
	@Parameter(property = "encoder.sourceFile", required = true)
	private String sourceFilePath;

	@Override
	public void execute() throws MojoExecutionException {
		getLog().info("Starting ASCII encoding process... sourceFilePath: " + sourceFilePath);

		Properties properties = new Properties();
		try (InputStreamReader reader = new InputStreamReader(new FileInputStream(sourceFilePath), StandardCharsets.UTF_8)) {
			properties.load(reader);
		} catch (IOException e) {
			throw new MojoExecutionException(e);
		}

		storePropertis(properties);
		getLog().info("End ASCII encoding process...");
	}

	private void storePropertis(final Properties properties) throws MojoExecutionException {
		try (OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(sourceFilePath, false), StandardCharsets.UTF_8)) {
			for (String key : properties.stringPropertyNames()) {
				String value = properties.getProperty(key);
				writer.write(key + "=" + CustomEncoder.escapeUnicode(value) + "\n");
			}
		} catch (IOException e) {
			throw new MojoExecutionException(e);
		}
	}


}