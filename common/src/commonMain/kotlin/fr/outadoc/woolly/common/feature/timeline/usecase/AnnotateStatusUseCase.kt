package fr.outadoc.woolly.common.feature.timeline.usecase

import fr.outadoc.mastodonk.api.entity.Status
import fr.outadoc.woolly.common.feature.timeline.AnnotatedStatus
import fr.outadoc.woolly.htmltext.HtmlParser

class AnnotateStatusUseCase(private val parser: HtmlParser) {

    operator fun invoke(status: Status): AnnotatedStatus {
        return AnnotatedStatus(
            status,
            parser.parse(status.content)
        )
    }
}