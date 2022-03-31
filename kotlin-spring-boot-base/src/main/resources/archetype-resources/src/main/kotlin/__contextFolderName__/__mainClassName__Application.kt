package ${package}.${contextFolderName}

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.ComponentScan

@SpringBootApplication
@ComponentScan("${package}.${contextFolderName}")
class ${mainClassName}Application

fun main(args: Array<String>) {
	runApplication<${mainClassName}Application>(*args)
}
