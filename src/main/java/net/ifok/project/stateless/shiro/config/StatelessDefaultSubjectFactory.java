package net.ifok.project.stateless.shiro.config;

import org.apache.shiro.subject.Subject;
import org.apache.shiro.subject.SubjectContext;
import org.apache.shiro.web.mgt.DefaultWebSubjectFactory;

/**
 * Subject工厂重写
 * 
 * @author xq
 *
 */
public class StatelessDefaultSubjectFactory extends DefaultWebSubjectFactory {

	@Override
	public Subject createSubject(SubjectContext context) {
		// 不创建session.
		context.setSessionCreationEnabled(false);
		return super.createSubject(context);
	}

}
