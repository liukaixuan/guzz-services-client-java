package com.guzzservices.velocity;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Properties;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.context.InternalContextAdapter;
import org.apache.velocity.exception.MethodInvocationException;
import org.apache.velocity.exception.ParseErrorException;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.apache.velocity.exception.TemplateInitException;
import org.apache.velocity.runtime.RuntimeServices;
import org.apache.velocity.runtime.directive.Directive;
import org.apache.velocity.runtime.parser.node.Node;
import org.guzz.Configuration;
import org.guzz.GuzzContext;
import org.guzz.api.velocity.GuzzBoundaryDirective;
import org.guzz.api.velocity.GuzzCountDirective;
import org.guzz.api.velocity.GuzzIncDirective;
import org.guzz.api.velocity.GuzzListDirective;
import org.guzz.api.velocity.SummonDirective;

public class MyVelocityDirective extends Directive {
	@Override
	public void init(RuntimeServices rs, InternalContextAdapter context, Node node) throws TemplateInitException {
		super.init(rs, context, node);

		String appName = (String) rs.getApplicationAttribute("appName");

		System.out.println("appName=" + appName);
	}

	public static void main(String args[]) throws Exception {
		/* first, we init the runtime engine. Defaults are fine. */
		GuzzContext gc = new Configuration("classpath:com/guzzservices/velocity/guzz.xml").newGuzzContext();

		Properties p = new Properties();
		p.setProperty("file.resource.loader.class", "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");// 
		p.setProperty("userdirective", MyVelocityDirective.class.getName()
				+ "," + GuzzBoundaryDirective.class.getName()
				+ ", " + GuzzListDirective.class.getName()
				+ ", " + GuzzIncDirective.class.getName()
				+ ", " + GuzzCountDirective.class.getName()
		
		);

		VelocityEngine ve = new VelocityEngine();
		ve.setApplicationAttribute("appName", "hello app name!");
		ve.setApplicationAttribute(SummonDirective.GUZZ_CONTEXT_NAME, gc) ;

		ve.init(p);

		Template template;

		template = ve.getTemplate("com/guzzservices/velocity/testtemplate.vm");

		for (int i = 0; i < 3; i++) {
			VelocityContext context = new VelocityContext();
			context.put("name", "Velocity");
			context.put("project", "Jakarta");

			/* lets render a template */

			StringWriter w = new StringWriter();
			template.merge(context, w);

			String s = "We are using $project $name to render this.";
			// w = new StringWriter();
			System.out.println(" string : " + w);
		}

		gc.shutdown();

	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return "truncate";
	}

	@Override
	public int getType() {
		// TODO Auto-generated method stub
		return LINE;
	}

	@Override
	public boolean render(InternalContextAdapter context, Writer writer, Node node) throws IOException, ResourceNotFoundException,
			ParseErrorException, MethodInvocationException {
		// setting default params
		String truncateMe = null;
		int maxLength = 10;
		String suffix = null;
		Boolean truncateAtWord = false;

		// reading params
		if (node.jjtGetChild(0) != null) {
			truncateMe = String.valueOf(node.jjtGetChild(0).value(context));
		}

		if (node.jjtGetChild(1) != null) {
			maxLength = (Integer) node.jjtGetChild(1).value(context);
		}

		if (node.jjtGetChild(2) != null) {
			suffix = String.valueOf(node.jjtGetChild(2).value(context));
		}

		if (node.jjtGetChild(3) != null) {
			truncateAtWord = (Boolean) node.jjtGetChild(3).value(context);
		}

		if (node.jjtGetNumChildren() > 4) {
			if (node.jjtGetChild(4) != null) {
				Object coll = node.jjtGetChild(4).value(context);

				System.out.println("collection passed in:" + coll);
			}
		}

		System.out.println("time in java:" + context.get("time"));

		// truncate and write result to writer
		writer.write(truncate(truncateMe, maxLength, suffix, truncateAtWord));
		writer.flush();

		context.put("time", System.currentTimeMillis() + new Object().toString());

		return true;

	}

	public String truncate(String truncateMe, int maxLength, String suffix, boolean truncateAtWord) {
		if (truncateMe == null || maxLength <= 0) {
			return null;
		}

		if (truncateMe.length() <= maxLength) {
			return truncateMe;
		}
		if (suffix == null || maxLength - suffix.length() <= 0) {
			// either no need or no room for suffix
			return truncateMe.substring(0, maxLength);
		}
		if (truncateAtWord) {
			// find the latest space within maxLength
			int lastSpace = truncateMe.substring(0, maxLength - suffix.length() + 1).lastIndexOf(" ");
			if (lastSpace > suffix.length()) {
				return truncateMe.substring(0, lastSpace) + suffix;
			}
		}
		// truncate to exact character and append suffix
		return truncateMe.substring(0, maxLength - suffix.length()) + suffix;

	}

}
