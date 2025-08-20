package com.yto.ylp.maven.plugin;

import com.yto.ylp.maven.utils.CustomEncoder;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.apache.maven.project.MavenProject;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Properties;

@Mojo(name = "merge-properties", requiresDependencyResolution = ResolutionScope.RUNTIME, defaultPhase = LifecyclePhase.COMPILE)
public class PropertiesMergerMojo extends AbstractMojo {

	@Parameter(defaultValue = "${project}", required = true, readonly = true)
	private MavenProject project;

	@Parameter(defaultValue = "${session}", readonly = true, required = true)
	private MavenSession session;

	@Parameter(property = "outputFile", defaultValue = "${project.build.outputDirectory}/i18n/messages.properties")
	private File outputFile;

	public void execute() throws MojoExecutionException {
		getLog().info("start merge properties file");
		// 创建一个 Properties 对象来保存所有合并的属性
		Properties mergedProperties = new Properties();

		List<MavenProject> allProjects = session.getAllProjects();

		getLog().info("get all modules:" + allProjects.stream().map(MavenProject::getName).reduce(" ", (a, b) -> a + " " + b));
		// 遍历所有模块
		for (MavenProject p : allProjects) {

			String propertiesFilePath = p.getBuild().getOutputDirectory() + "/api_codes.properties";
			File propertiesFile = new File(propertiesFilePath);
			if (propertiesFile.exists()) {
				getLog().info("Found properties file in module: " + p.getArtifactId());
				try (InputStreamReader isr = new InputStreamReader(new FileInputStream(propertiesFilePath), StandardCharsets.UTF_8)) {
					Properties tempProps = new Properties();
					tempProps.load(isr);
					// 将当前模块的属性添加到总的属性对象中
					mergedProperties.putAll(tempProps);
				} catch (IOException e) {
					throw new MojoExecutionException("Error reading properties file: " + propertiesFile.getAbsolutePath(), e);
				}
//				propertiesFile.delete();
			}
		}

		// 确保输出目录存在
		if (!outputFile.getParentFile().exists()) {
			outputFile.getParentFile().mkdirs();
		}

		try (OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(outputFile, false), StandardCharsets.UTF_8)) {
			for (String key : mergedProperties.stringPropertyNames()) {
				String value = mergedProperties.getProperty(key);
				writer.write(key + "=" + CustomEncoder.escapeUnicode(value) + "\n");
			}
		} catch (IOException e) {
			throw new MojoExecutionException(e);
		}
		getLog().info("merged properties file to :" + outputFile.getAbsolutePath());
	}
}