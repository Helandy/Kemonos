package su.afk.kemonos.posts.presenter.pagePopularPosts.views

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import su.afk.kemonos.posts.R
import su.afk.kemonos.posts.domain.model.popular.Period
import su.afk.kemonos.posts.domain.model.popular.PopularNavSlot
import su.afk.kemonos.posts.presenter.pagePopularPosts.PopularPostsState
import su.afk.kemonos.posts.presenter.pagePopularPosts.utils.formatRangeDesc
import su.afk.kemonos.posts.presenter.pagePopularPosts.utils.isNextAllowed
import su.afk.kemonos.posts.presenter.pagePopularPosts.utils.tripleFor

@Composable
internal fun PopularPeriodsPanel(
    state: PopularPostsState,
    onSlotClick: (Period, PopularNavSlot) -> Unit,
) {
    val info = state.popularPostsInfo
    val nav = info?.navigationDates
    val propsToday = state.popularProps?.today

    val context = LocalContext.current
    val rangeText = remember(info?.minDate, info?.maxDate) {
        formatRangeDesc(
            context = context,
            min = info?.minDate,
            max = info?.maxDate
        )
    }

    Surface(
        modifier = Modifier.fillMaxWidth().padding(bottom = 6.dp, top = 6.dp),
        shape = RoundedCornerShape(16.dp),
        tonalElevation = 2.dp,
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 6.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if (rangeText.isNotEmpty()) {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = rangeText,
                        fontSize = MaterialTheme.typography.labelLarge.fontSize,
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            PeriodRowButtons(
                date = propsToday,
                title = stringResource(R.string.popular_period_day),
                period = Period.DAY,
                triple = nav.tripleFor(Period.DAY),
                selected = state.popularPeriod == Period.DAY,
                onSlotClick = onSlotClick
            )
            PeriodRowButtons(
                date = propsToday,
                title = stringResource(R.string.popular_period_week),
                period = Period.WEEK,
                triple = nav.tripleFor(Period.WEEK),
                selected = state.popularPeriod == Period.WEEK,
                onSlotClick = onSlotClick
            )
            PeriodRowButtons(
                date = propsToday,
                title = stringResource(R.string.popular_period_month),
                period = Period.MONTH,
                triple = nav.tripleFor(Period.MONTH),
                selected = state.popularPeriod == Period.MONTH,
                onSlotClick = onSlotClick
            )
        }
    }
}

@Composable
private fun PeriodRowButtons(
    date: String?,
    title: String,
    period: Period,
    triple: Triple<String?, String?, String?>?,
    selected: Boolean,
    onSlotClick: (Period, PopularNavSlot) -> Unit,
) {
    val hasPrev = triple?.first != null
    val hasCurrent = triple?.second != null

    val hasNext = isNextAllowed(
        nextDate = triple?.third,
        date = date
    )

    PopularPeriodRow(
        label = title,
        selected = selected,
        hasPrev = hasPrev,
        hasNext = hasNext,
        onPrev = {
            if (hasPrev) onSlotClick(period, PopularNavSlot.PREV)
        },
        onCenter = {
            if (hasCurrent) onSlotClick(period, PopularNavSlot.CURRENT)
        },
        onNext = {
            if (hasNext) onSlotClick(period, PopularNavSlot.NEXT)
        }
    )
}

@Composable
internal fun PopularPeriodRow(
    label: String,
    selected: Boolean,
    hasPrev: Boolean,
    hasNext: Boolean,
    onPrev: () -> Unit,
    onCenter: () -> Unit,
    onNext: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        PeriodArrowButton(
            enabled = hasPrev,
            onClick = onPrev,
            icon = Icons.AutoMirrored.Rounded.ArrowBack
        )

        PeriodCenterButton(
            text = label.uppercase(),
            selected = selected,
            onClick = onCenter,
            clickable = true,
            modifier = Modifier.weight(1f)
        )

        PeriodArrowButton(
            enabled = hasNext,
            onClick = onNext,
            icon = Icons.AutoMirrored.Rounded.ArrowForward
        )
    }
}

@Composable
internal fun PeriodArrowButton(
    enabled: Boolean,
    onClick: () -> Unit,
    icon: ImageVector,
    modifier: Modifier = Modifier,
) {
    FilledTonalIconButton(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier
            .height(40.dp)
            .width(64.dp),
        shape = RoundedCornerShape(999.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null
        )
    }
}

@Composable
internal fun PeriodCenterButton(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    clickable: Boolean = true,
) {
    val clickMod = if (clickable) Modifier.clickable(onClick = onClick) else Modifier

    Surface(
        shape = RoundedCornerShape(999.dp),
        color = if (selected)
            MaterialTheme.colorScheme.primaryContainer
        else
            MaterialTheme.colorScheme.surfaceVariant,
        border = BorderStroke(
            1.dp,
            if (selected) MaterialTheme.colorScheme.primary
            else MaterialTheme.colorScheme.outlineVariant
        ),
        modifier = modifier
            .height(40.dp)
            .then(clickMod)
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(
                text = text,
                style = MaterialTheme.typography.labelLarge,
                color = if (selected)
                    MaterialTheme.colorScheme.onPrimaryContainer
                else
                    MaterialTheme.colorScheme.onSurface
            )
        }
    }
}