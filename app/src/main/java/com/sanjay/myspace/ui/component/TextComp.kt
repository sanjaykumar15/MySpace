package com.sanjay.myspace.ui.component

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withLink
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp
import com.sanjay.myspace.ui.theme.PrimaryClr
import com.sanjay.myspace.ui.theme.TextDark

@Composable
fun DefaultText(
    text: String,
    fontSize: TextUnit = 14.sp,
) {
    Text(
        text = text,
        fontSize = fontSize,
        lineHeight = fontSize
    )
}

@Composable
fun ErrorText(
    text: String,
    modifier: Modifier,
    fontSize: TextUnit = 12.sp,
    fontStyle: FontStyle = FontStyle.Italic,
    fontWeight: FontWeight = FontWeight.Normal,
    textAlign: TextAlign = TextAlign.Start,
    textColor: Color = Color.Red,
) {
    Text(
        text = text,
        modifier = modifier,
        fontSize = fontSize,
        fontWeight = fontWeight,
        fontStyle = fontStyle,
        color = textColor,
        textAlign = textAlign,
        lineHeight = 12.sp
    )
}

@Composable
fun Text12(
    text: String,
    modifier: Modifier,
    fontStyle: FontStyle = FontStyle.Normal,
    fontWeight: FontWeight = FontWeight.Normal,
    textAlign: TextAlign = TextAlign.Start,
    textColor: Color = Color.Black,
    minLines: Int = 1,
    maxLines: Int = Integer.MAX_VALUE,
) {
    Text(
        text = text,
        modifier = modifier,
        fontSize = 12.sp,
        fontWeight = fontWeight,
        fontStyle = fontStyle,
        color = textColor,
        textAlign = textAlign,
        minLines = minLines,
        maxLines = maxLines,
        lineHeight = 12.sp
    )
}

@Composable
fun Text14(
    text: String,
    modifier: Modifier,
    fontStyle: FontStyle = FontStyle.Normal,
    fontWeight: FontWeight = FontWeight.Normal,
    textAlign: TextAlign = TextAlign.Start,
    textColor: Color = Color.Black,
) {
    Text(
        text = text,
        modifier = modifier,
        fontSize = 14.sp,
        fontWeight = fontWeight,
        fontStyle = fontStyle,
        color = textColor,
        textAlign = textAlign,
        lineHeight = 14.sp
    )
}

@Composable
fun Text18(
    text: String,
    modifier: Modifier,
    fontStyle: FontStyle = FontStyle.Normal,
    fontWeight: FontWeight = FontWeight.Normal,
    textAlign: TextAlign = TextAlign.Start,
    textColor: Color = TextDark,
) {
    Text(
        text = text,
        modifier = modifier,
        fontSize = 18.sp,
        fontWeight = fontWeight,
        fontStyle = fontStyle,
        color = textColor,
        textAlign = textAlign,
        overflow = TextOverflow.Clip,
        lineHeight = 18.sp
    )
}

@Composable
fun CustomText(
    text: String,
    modifier: Modifier,
    fontSize: TextUnit,
    fontStyle: FontStyle = FontStyle.Normal,
    fontWeight: FontWeight = FontWeight.Normal,
    fontFamily: FontFamily = FontFamily.Default,
    textAlign: TextAlign = TextAlign.Start,
    textColor: Color = TextDark,
    minLines: Int = 1,
    maxLines: Int = Integer.MAX_VALUE,
    textOverflow: TextOverflow = TextOverflow.Clip,
) {
    Text(
        text = text,
        modifier = modifier,
        fontSize = fontSize,
        fontWeight = fontWeight,
        fontStyle = fontStyle,
        fontFamily = fontFamily,
        color = textColor,
        textAlign = textAlign,
        minLines = minLines,
        maxLines = maxLines,
        overflow = textOverflow,
        lineHeight = fontSize,
    )
}

data class ClickableTextData(
    var text: String,
    var value: String,
    var clickable: Boolean = false,
    var isUrl: Boolean = false,
    var textLinkColor: Color = PrimaryClr,
)

@Composable
fun ClickableTextView(
    clickableTextData: List<ClickableTextData>,
    modifier: Modifier,
    fontSize: TextUnit = 12.sp,
    onTextClicked: (String) -> Unit,
) {
    val annotatedString = buildAnnotatedString {
        for (data in clickableTextData) {
            if (data.clickable) {
                withLink(
                    if (data.isUrl) {
                        LinkAnnotation.Url(
                            url = data.value,
                            styles = TextLinkStyles(
                                style = SpanStyle(
                                    color = data.textLinkColor,
                                    textDecoration = TextDecoration.Underline
                                )
                            )
                        )
                    } else {
                        LinkAnnotation.Clickable(
                            tag = data.value,
                            styles = TextLinkStyles(
                                style = SpanStyle(color = data.textLinkColor)
                            ),
                            linkInteractionListener = {
                                onTextClicked(data.value)
                            }
                        )
                    }
                ) {
                    append(data.text)
                }
            } else {
                append(data.text)
            }
        }
    }

    Text(
        text = annotatedString,
        modifier = modifier,
        fontSize = fontSize,
        lineHeight = fontSize
    )
}